package com.rsetiapp.common

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.karumi.dexter.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.model.request.BankIFSCSearchReq
import com.rsetiapp.common.model.response.BankDetailsList
import com.rsetiapp.common.model.response.BankIFSCSearchRes
import com.rsetiapp.common.model.response.CandidateSearchData
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.core.util.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MySattelementBottomSheet: BottomSheetDialogFragment() {
    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var progressBar: View
    private lateinit var bankName: TextView
    private lateinit var branchName: TextView
    private lateinit var accountNo: EditText
    private lateinit var etCity: EditText
    private lateinit var etSelfInvestment: EditText
    private lateinit var etReason: EditText
    private lateinit var etCreditFromBank: EditText
    private lateinit var btnSettledSubmit: TextView
    private lateinit var etEmploymentGiven: EditText
    private lateinit var ivSettlementPhoto: ImageView
    private lateinit var ivPassbookCopy: ImageView
    private lateinit var ivAppointmentLetter: ImageView
    private lateinit var spinnerStatus: AutoCompleteTextView
    private lateinit var spinnerAccount: AutoCompleteTextView
    private lateinit var spinnerEarning: AutoCompleteTextView
    private lateinit var spinnerFamimyMemberJob : AutoCompleteTextView
    private lateinit var  llselfInvestment : LinearLayout
    private lateinit var  llBankinvestment : LinearLayout
    private  lateinit var llTotal :LinearLayout




    private lateinit var selfAndServiceAdapter: ArrayAdapter<String>
    private val selfAndServiceList = listOf("SelfSettled", "Settled In service")


    private  lateinit var accountStatusAdapter: ArrayAdapter<String>
    private val accountStatusList = listOf("Active", "InActive")


    private  lateinit var familyMemberPartTimeJobAdapter: ArrayAdapter<String>
    private val familyMemberPartTimeJobList = listOf("Yes", "No")

    private var ifscSearchList: List<BankDetailsList> = listOf()

    //Selected all values
    var selectedBankCode =0
    var SelectedBranchCode = 0
    var accLenghth = 0
    private var selectedStatusItem=""
    private var selectedSelfInvestmentItem=""
    private var SelectedCreditFromBankItem=""
    private var selectedTotal=""
    private var selectedUpperCaseIfscText=""
    private var selectedLoanAcc=""
    private var selectedCity=""
    private var selectedReason=""
    private var selectdeAccountStatus=""
    private var selectedEarnings=""
    private var selectedEmploymentGiven=""
    private var selectedFamilyMemberPartTime=""
    private  var selectedSettlementPhoto=""
    private  var selectedPassbookCopy=""
    private  var selectedAppointmentLetter=""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.settlement_bottomsheet_layout, container, false)
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ifscEt = view.findViewById<EditText>(R.id.etIfscCode)
        val ifscBtn = view.findViewById<Button>(R.id.ifscButton)
        bankName = view.findViewById<TextView>(R.id.etBankName)
        branchName = view.findViewById<TextView>(R.id.etBranchName)
        progressBar = view.findViewById<View>(R.id.progressBar)
        accountNo = view.findViewById(R.id.etBankAcNo)
        spinnerStatus = view.findViewById<AutoCompleteTextView>(R.id.spinnerStatus)
        spinnerAccount = view.findViewById<AutoCompleteTextView>(R.id.spinnerAccountStatus)
        spinnerFamimyMemberJob = view.findViewById<AutoCompleteTextView>(R.id.spinnerFamilyMemberPartTime)
        llselfInvestment = view.findViewById(R.id.llselfinvestment)
        llBankinvestment = view.findViewById(R.id.llBankinvestment)
        llTotal = view.findViewById(R.id.llTotal)
        etCity=view.findViewById(R.id.etCity)
        etSelfInvestment=view.findViewById(R.id.etselfInvestment)
        etReason=view.findViewById(R.id.etReason)
        etCreditFromBank=view.findViewById(R.id.etCredit)
        etEmploymentGiven=view.findViewById(R.id.etEmploymentGiven)
        ivSettlementPhoto=view.findViewById(R.id.settlmentPhoto)
        ivPassbookCopy=view.findViewById(R.id.passbookPhoto)
        ivAppointmentLetter=view.findViewById(R.id.appointmentLetter)
        spinnerEarning=view.findViewById(R.id.spinnerEarningsIncome)
        btnSettledSubmit=view.findViewById(R.id.btnSettled)



        //Adapter Follow Up Status
        selfAndServiceAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, selfAndServiceList
        )
       spinnerStatus.setAdapter(selfAndServiceAdapter)





        accountStatusAdapter =ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,accountStatusList)
        spinnerAccount.setAdapter(accountStatusAdapter)



        familyMemberPartTimeJobAdapter =ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,familyMemberPartTimeJobList)
        spinnerFamimyMemberJob.setAdapter(familyMemberPartTimeJobAdapter)





        // Set listener for spinnerStatus
        spinnerStatus.setOnItemClickListener { parent, view, position, id ->
            selectedStatusItem = parent.getItemAtPosition(position) as String

            // Check if the selected item is "Settled In service"
            if (selectedStatusItem.equals("Settled In service", ignoreCase = true)) {
                // Hide the self-investment section
                llselfInvestment.visibility = View.GONE
                llBankinvestment.visibility =View.GONE
                llTotal.visibility = View.GONE
             selectedSelfInvestmentItem=""
             SelectedCreditFromBankItem=""
              selectedTotal=""


            }
             else if(selectedStatusItem.equals("SelfSettled", ignoreCase = true )) {
                 llselfInvestment.visibility = View.VISIBLE
                llBankinvestment.visibility = View.VISIBLE
                llTotal.visibility = View.VISIBLE



            }

        }



        ifscBtn.setOnClickListener {

            if(ifscEt.text.toString().isNotEmpty()){
                val inputText = ifscEt.text.toString()
                 selectedUpperCaseIfscText = inputText.uppercase()
                commonViewModel.getbankIFSCAPI(
                    BankIFSCSearchReq(
                        BuildConfig.VERSION_NAME,
                        selectedUpperCaseIfscText
                    )
                )

                collectBankDetailResponse()



            }
            else
                toastShort("Please Enter Ifsc Code")

        }



    }

    private fun collectBankDetailResponse() {
        lifecycleScope.launch {
            commonViewModel.getbankIFSCAPI.collectLatest { it ->
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getBankDetails ->
                            if (getBankDetails.responseCode == 200) {
                                ifscSearchList= getBankDetails.bankDetailsList


                                for (x in ifscSearchList) {
                                    bankName.text= x.bankName
                                    branchName.text= x.branchName
                                    selectedBankCode= x.bankCode
                                    SelectedBranchCode= x.branchCode
                                    accLenghth= x.branchCode
                                }
                                accountNo.filters = arrayOf(InputFilter.LengthFilter(accLenghth))

                            }
                            else if (getBankDetails.responseCode == 301) {
                                Toast.makeText(requireContext(), getBankDetails.responseMsg, Toast.LENGTH_SHORT).show()


                            }

                            else if (getBankDetails.responseCode == 302) {

                                Toast.makeText(requireContext(), getBankDetails.responseMsg, Toast.LENGTH_SHORT).show()
                                bankName.text= ""
                                branchName.text= ""
                                selectedBankCode= 0
                                SelectedBranchCode= 0
                                accLenghth= 0

                            }

                        } ?:   Toast.makeText(requireContext(), "Internal Server Error", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

}