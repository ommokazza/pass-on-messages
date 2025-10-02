package net.ommoks.azza.android.app.passonnotifications.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.ommoks.azza.android.app.passonnotifications.R
import net.ommoks.azza.android.app.passonnotifications.data.model.Filter
import net.ommoks.azza.android.app.passonnotifications.data.model.FilterRule
import net.ommoks.azza.android.app.passonnotifications.data.model.RuleType
import net.ommoks.azza.android.app.passonnotifications.data.model.getStringRes
import java.io.Serializable

class EditFilterDialog : DialogFragment() {

    // 수정한 필터 정보를 MainFragment로 전달하기 위한 리스너
    interface EditFilterDialogListener {
        fun onFilterSaved(filter: Filter)
    }

    private var listener: EditFilterDialogListener? = null
    private lateinit var filter: Filter
    private lateinit var rulesContainer: LinearLayout

    companion object {
        private const val ARG_FILTER = "filter"

        fun newInstance(filter: Filter, listener: EditFilterDialogListener): EditFilterDialog {
            val dialog = EditFilterDialog()
            dialog.listener = listener
            // Filter 객체는 Serializable이므로 Bundle에 넣어 전달
            val args = Bundle().apply {
                putSerializable(ARG_FILTER, filter as Serializable)
            }
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 전달받은 filter 객체를 복사해서 사용 (원본을 직접 수정하지 않음)
        filter = (arguments?.getSerializable(ARG_FILTER) as? Filter)?.copy(
            rules = (arguments?.getSerializable(ARG_FILTER) as Filter).rules.map { it.copy() }.toMutableList()
        ) ?: throw IllegalStateException("Filter cannot be null")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_filter, null)
        rulesContainer = view.findViewById(R.id.rules_container)

        val filterNameEdit: EditText = view.findViewById(R.id.filter_name_edit)
        val passOnToEdit: EditText = view.findViewById(R.id.pass_on_to_edit)
        val addRuleButton: Button = view.findViewById(R.id.add_rule_button)

        // 1. 기존 데이터로 UI 초기화
        filterNameEdit.setText(filter.name)
        passOnToEdit.setText(filter.passOnTo)
        updateRulesUI()

        // 2. UI 변경 리스너 설정
        filterNameEdit.doOnTextChanged { text, _, _, _ ->
            filter.name = text.toString()
        }
        passOnToEdit.doOnTextChanged { text, _, _, _ ->
            filter.passOnTo = text.toString()
        }
        addRuleButton.setOnClickListener {
            filter.rules.add(FilterRule(type = RuleType.TextContains, phrase = ""))
            updateRulesUI() // 규칙 목록 UI 새로고침
        }

        // 3. 다이얼로그 생성 및 반환
        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setTitle(R.string.modify_filter)
            .setPositiveButton(R.string.save) { _, _ ->
                // 저장 버튼 클릭 시 리스너를 통해 MainFragment에 데이터 전달
                listener?.onFilterSaved(filter)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun updateRulesUI() {
        rulesContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        filter.rules.forEach { rule ->
            val ruleView = inflater.inflate(R.layout.item_rule, rulesContainer, false)
            val ruleTypeSpinner: Spinner = ruleView.findViewById(R.id.rule_type_spinner)
            val rulePhrase: EditText = ruleView.findViewById(R.id.rule_phrase)
            val deleteRuleIcon: ImageView = ruleView.findViewById(R.id.delete_rule_icon)

            // Spinner 설정
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                RuleType.entries.map { it.getStringRes(requireContext()) }
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            ruleTypeSpinner.adapter = spinnerAdapter
            ruleTypeSpinner.setSelection(spinnerAdapter.getPosition(rule.type.getStringRes(requireContext())))
            ruleTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedRuleType = RuleType.entries[position]
                    rule.type = selectedRuleType
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            // EditText 설정
            rulePhrase.setText(rule.phrase)
            rulePhrase.doOnTextChanged { text, _, _, _ ->
                rule.phrase = text.toString()
            }

            // 삭제 버튼 설정
            deleteRuleIcon.setOnClickListener {
                filter.rules.remove(rule)
                updateRulesUI() // UI 새로고침
            }

            rulesContainer.addView(ruleView)
        }
    }
}