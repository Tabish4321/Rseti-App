package com.rsetiapp.common.fragments
import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.rsetiapp.common.model.response.WrappedList
import com.rsetiapp.common.model.response.BlockList
import com.rsetiapp.common.model.response.DistrictList
import com.rsetiapp.common.model.response.GrampanchayatList
import com.rsetiapp.common.model.response.VillageList
import com.rsetiapp.common.CommonViewModel
import com.rsetiapp.core.basecomponent.BaseFragment
import com.rsetiapp.core.util.Resource
import com.rsetiapp.core.util.toastShort
import com.rsetiapp.databinding.FragmentEapAwarnessBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rsetiapp.BuildConfig
import com.rsetiapp.R
import com.rsetiapp.common.CandidateBottomSheetFragment
import com.rsetiapp.common.CandidateUpdateListener
import com.rsetiapp.common.adapter.CandidateAdapter
import com.rsetiapp.common.model.request.Candidate
import com.rsetiapp.common.model.request.EAPInsertRequest
import com.rsetiapp.common.model.response.Program
import com.rsetiapp.core.util.AppUtil
import com.rsetiapp.core.util.AppUtil.getCurrentDate
import com.rsetiapp.core.util.AppUtil.hasStoragePermission
import com.rsetiapp.core.util.UserPreferences
import com.rsetiapp.core.util.toastLong
import com.rsetiapp.core.util.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class EAPAwarnessFormFragment  : BaseFragment<FragmentEapAwarnessBinding>(FragmentEapAwarnessBinding::inflate),
    CandidateUpdateListener {

    private var formName=""
    private var eapId=""
    private var selectedDate=""
    private var selectedTotalParticipants=""
    private var selectedNameOfNGO=""
    private var selectedNoOfAppExpectedNextMonth=""
    private var selectedBrief=""
    private var image1Base64=""
    private var image2Base64=""
    private var locationLatLang=""
    private var locationAddress=""
    private var imageUri: Uri? = null
    private var currentImageView: ImageView? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: CandidateAdapter
    private val candidateList = mutableListOf<Candidate>()
    private val commonViewModel: CommonViewModel by activityViewModels()
    private lateinit var locationSettingLauncher: ActivityResultLauncher<IntentSenderRequest>

    //State Var
    private lateinit var stateAdapter: ArrayAdapter<String>
    private var stateList: MutableList<WrappedList> = mutableListOf()
    private var state = ArrayList<String>()
    private var stateCode = ArrayList<String>()
    private var stateLgdCode = ArrayList<String>()
    private var selectedStateCodeItem = ""
    private var selectedStateLgdCodeItem = ""
    private var selectedStateItem = ""
    private var counts = ""
    private var latitude : Double? = null
    private var longitude : Double? = null

    // district var
    private var districtList: MutableList<DistrictList> = mutableListOf()
    private lateinit var districtAdapter: ArrayAdapter<String>
    private var district = ArrayList<String>()
    private var districtCode = ArrayList<String>()
    private var districtLgdCode = ArrayList<String>()
    private var selectedDistrictCodeItem = ""
    private var selectedDistrictLgdCodeItem = ""
    private var selectedDistrictItem = ""


    //block var
    private var blockList: MutableList<BlockList> = mutableListOf()
    private lateinit var blockAdapter: ArrayAdapter<String>
    private var block = ArrayList<String>()
    private var blockCode = ArrayList<String>()
    private var blockLgdCode = ArrayList<String>()
    private var selectedBlockCodeItem = ""
    private var selectedbBlockLgdCodeItem = ""
    private var selectedBlockItem = ""


    //GP var
    private var gpList: MutableList<GrampanchayatList> = mutableListOf()
    private lateinit var gpAdapter: ArrayAdapter<String>
    private var gp = ArrayList<String>()
    private var gpCode = ArrayList<String>()
    private var gpLgdCode = ArrayList<String>()
    private var selectedGpCodeItem = ""
    private var selectedbGpLgdCodeItem = ""
    private var selectedGpItem = ""


    private var orgCode = ""
    private var orgName = ""
    private var instituteName = ""
    private var instituteCode = ""
    private var officialName = ""
    private var designationName = ""

    //Village var
    private var villageList: MutableList<VillageList> = mutableListOf()
    private lateinit var villageAdapter: ArrayAdapter<String>
    private var village = ArrayList<String>()
    private var villageCode = ArrayList<String>()
    private var villageLgdCode = ArrayList<String>()
    private var selectedVillageCodeItem = ""
    private var selectedbVillageLgdCodeItem = ""
    private var selectedVillageItem = ""


    //Program name var
    private var programNameList: List<Program> = mutableListOf()
    private lateinit var programNameAdapter: ArrayAdapter<String>
    private var programName = ArrayList<String>()
    private var programNameCode = ArrayList<String>()
    private var selectedprogramNameCodeItem = ""
    private var selectedprogramNameItem = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        formName = arguments?.getString("formName").toString()
        eapId = arguments?.getString("eapId").toString()
        userPreferences = UserPreferences(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        init()

    }

    private fun init(){


        locationSettingLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // GPS was enabled by the user
            } else {
                // GPS was not enabled
            }
        }
        checkAndRequestStoragePermissions()

        checkAndPromptGPS()

        // checkAndRequestPermissions()
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        val candidateCountTextView = view?.findViewById<TextView>(R.id.candidteCount)

        @SuppressLint("SetTextI18n")
        fun updateCandidateCount(count: Int) {
            candidateCountTextView?.text = "Candidates: "+count.toString()
            counts=count.toString()



        }

        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        adapter = CandidateAdapter(
            candidateList,
            onDelete = { position ->
                if (position in candidateList.indices) {
                    candidateList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, candidateList.size)
                    updateCandidateCount(candidateList.size)
                }
            },
            onUpdateCount = { count ->
                updateCandidateCount(count)
            }
        )

// Set adapter after initializing RecyclerView
        recyclerView?.adapter = adapter

// Set initial count after adapter is set
        updateCandidateCount(candidateList.size)

        commonViewModel.getStateListApi(AppUtil.getSavedTokenPreference(requireContext()),userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))
        commonViewModel.getEapAutoFetchListAPI(AppUtil.getSavedTokenPreference(requireContext()),userPreferences.getUseID(), BuildConfig.VERSION_NAME,AppUtil.getAndroidId(requireContext()))
        commonViewModel.getProgramListAPI(AppUtil.getSavedTokenPreference(requireContext()),userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))
        collectProgramNameResponse()
        collectEapAutoFetchResponse()
        collectStateResponse()
        collectDistrictResponse()
        collectBlockResponse()
        collectGpResponse()
        collectVillageResponse()
        listener()
    }
    @SuppressLint("SetTextI18n")
    private fun listener(){

        val currentDate= getCurrentDate()
        binding.tvDate.text = currentDate
        selectedDate= currentDate

        binding.eapIdName.text= eapId
        //Submit Button

        binding.btnSubmit.setOnClickListener {

            selectedTotalParticipants =binding.etTotalParticipant.text.toString()
            selectedNameOfNGO =binding.etNameOfOrgt.text.toString()

            selectedNoOfAppExpectedNextMonth =binding.etNoOfAppExpec.text.toString()
            selectedBrief =binding.etBrief.text.toString()





            if (selectedDate.isNotEmpty()&& selectedTotalParticipants.isNotEmpty()&& selectedNameOfNGO.isNotEmpty() && selectedprogramNameCodeItem.isNotEmpty() &&
                selectedStateCodeItem.isNotEmpty() &&  selectedDistrictCodeItem.isNotEmpty() &&  selectedBlockCodeItem.isNotEmpty() && selectedGpCodeItem.isNotEmpty()&&
                selectedVillageCodeItem.isNotEmpty() && selectedNoOfAppExpectedNextMonth.isNotEmpty() && selectedBrief.isNotEmpty()&& image1Base64.isNotEmpty()&&
                image2Base64.isNotEmpty()){



                val totalParticipant = binding.etTotalParticipant.text.toString()

                if (counts != totalParticipant) {

                    AppUtil.showAlertDialog(requireContext(),"Alert","Candidates should be equal to total participants")

                }


                else{

                    commonViewModel.insertEAPAPI(AppUtil.getSavedTokenPreference(requireContext()),EAPInsertRequest(AppUtil.getAndroidId(requireContext()),userPreferences.getUseID(),BuildConfig.VERSION_NAME,orgCode,eapId,instituteCode,selectedDate,selectedTotalParticipants,selectedNameOfNGO,officialName,designationName,
                        selectedprogramNameCodeItem,selectedStateCodeItem,selectedDistrictCodeItem,selectedBlockCodeItem,selectedGpCodeItem,selectedVillageCodeItem,
                        selectedNoOfAppExpectedNextMonth,selectedBrief,image1Base64,image2Base64,
                        latitude.toString(),
                        longitude.toString(),candidateList))
                    collectInsertResponse()


                }

            }

            else
                toastShort("Kindly fill all the fields first")


        }





        binding.btnAddCandidate.setOnClickListener {
            val bottomSheet = CandidateBottomSheetFragment(candidateList, adapter) { count ->

                 counts = count.toString()
                view?.findViewById<TextView>(R.id.candidteCount)?.text = "Candidates: $count"
            }
            val totalParticipant = binding.etTotalParticipant.text.toString()

            if (counts != totalParticipant) {
                bottomSheet.show(parentFragmentManager, "CandidateBottomSheet")
            }
            else


           AppUtil.showAlertDialog(requireContext(),"Limit Reached","You cannot add more candidates as the maximum number of participants has been reached.")

        }


        binding.tvFormName.text= formName
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }


        //Adapter Program

        programNameAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            programName
        )

        binding.spinnerProgramName.setAdapter(programNameAdapter)

        //Adapter State

        stateAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            state
        )

        binding.spinnerState.setAdapter(stateAdapter)
        //Adapter District setting

        districtAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            district
        )

        binding.spinnerDistrict.setAdapter(districtAdapter)


        //Adapter Block setting

        blockAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            block
        )

        binding.spinnerBlock.setAdapter(blockAdapter)

        //Adapter GP setting

        gpAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            gp
        )

        binding.spinnerGp.setAdapter(gpAdapter)


        //Adapter Village setting

        villageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            village
        )

        binding.spinnerVillage.setAdapter(villageAdapter)




        //Selection Of State,and so on


        binding.spinnerState.setOnItemClickListener { parent, view, position, id ->
            selectedStateItem = parent.getItemAtPosition(position).toString()
            if (position in state.indices) {

               block.clear()
                gp.clear()
                village.clear()

                selectedStateCodeItem = stateCode[position]
                selectedStateLgdCodeItem = stateLgdCode[position]
                commonViewModel.getDistrictListApi(AppUtil.getSavedTokenPreference(requireContext()),selectedStateCodeItem,userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))
                districtAdapter.notifyDataSetChanged()



                selectedDistrictCodeItem = ""
                selectedDistrictLgdCodeItem = ""
                selectedDistrictItem = ""
                binding.spinnerDistrict.clearFocus()
                binding.spinnerDistrict.setText("", false)


                selectedBlockCodeItem = ""
                selectedbBlockLgdCodeItem = ""
                selectedBlockItem = ""
                binding.spinnerBlock.clearFocus()
                binding.spinnerBlock.setText("", false)

                selectedGpCodeItem = ""
                selectedbGpLgdCodeItem = ""
                selectedGpItem = ""
                binding.spinnerGp.clearFocus()
                binding.spinnerGp.setText("", false)


                selectedVillageCodeItem = ""
                selectedbVillageLgdCodeItem = ""
                selectedVillageItem = ""
                binding.spinnerVillage.clearFocus()
                binding.spinnerVillage.setText("", false)



            } else {
                Toast.makeText(requireContext(), "Invalid selection", Toast.LENGTH_SHORT).show()
            }
        }

        //District selection
        binding.spinnerDistrict.setOnItemClickListener { parent, view, position, id ->
            selectedDistrictItem = parent.getItemAtPosition(position).toString()
            if (position in district.indices) {

                gp.clear()
                village.clear()

                selectedDistrictCodeItem = districtCode[position]
                selectedDistrictLgdCodeItem = districtLgdCode[position]
                commonViewModel.getBlockListApi(AppUtil.getSavedTokenPreference(requireContext()),selectedDistrictCodeItem,userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))
                gpAdapter.notifyDataSetChanged()

                selectedBlockCodeItem = ""
                selectedbBlockLgdCodeItem = ""
                selectedBlockItem = ""
                binding.spinnerBlock.clearFocus()
                binding.spinnerBlock.setText("", false)


                selectedGpCodeItem = ""
                selectedbGpLgdCodeItem = ""
                selectedGpItem = ""
                binding.spinnerGp.clearFocus()
                binding.spinnerGp.setText("", false)


                selectedVillageCodeItem = ""
                selectedbVillageLgdCodeItem = ""
                selectedVillageItem = ""
                binding.spinnerVillage.clearFocus()
                binding.spinnerVillage.setText("", false)

            } else {
                Toast.makeText(requireContext(), "Invalid selection", Toast.LENGTH_SHORT).show()
            }
        }


        //Block Spinner
        binding.spinnerBlock.setOnItemClickListener { parent, view, position, id ->
            selectedBlockItem = parent.getItemAtPosition(position).toString()
            if (position in block.indices) {
                village.clear()
                selectedBlockCodeItem = blockCode[position]
                selectedbBlockLgdCodeItem = blockLgdCode[position]
                commonViewModel.getGpListApi(AppUtil.getSavedTokenPreference(requireContext()),selectedBlockCodeItem,userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))
                selectedGpCodeItem = ""
                selectedbGpLgdCodeItem = ""
                selectedGpItem = ""
                binding.spinnerGp.clearFocus()
                binding.spinnerGp.setText("", false)
                selectedVillageCodeItem = ""
                selectedbVillageLgdCodeItem = ""
                selectedVillageItem = ""
                binding.spinnerVillage.clearFocus()
                binding.spinnerVillage.setText("", false)


            } else {
                Toast.makeText(requireContext(), "Invalid selection", Toast.LENGTH_SHORT).show()
            }
        }


        //GP Spinner
        binding.spinnerGp.setOnItemClickListener { parent, view, position, id ->
            selectedGpItem = parent.getItemAtPosition(position).toString()

            if (position in gp.indices) {
                selectedGpCodeItem = gpCode[position]
                selectedbGpLgdCodeItem = gpLgdCode[position]
                commonViewModel.getVillageListApi(AppUtil.getSavedTokenPreference(requireContext()),selectedGpCodeItem,userPreferences.getUseID(),AppUtil.getAndroidId(requireContext()))

                selectedVillageCodeItem = ""
                selectedbVillageLgdCodeItem = ""
                selectedVillageItem = ""
                binding.spinnerVillage.clearFocus()
                binding.spinnerVillage.setText("", false)

            } else {
                Toast.makeText(requireContext(), "Invalid selection", Toast.LENGTH_SHORT).show()
            }
        }


        //Village Spinner
        binding.spinnerVillage.setOnItemClickListener { parent, view, position, id ->
            selectedVillageItem = parent.getItemAtPosition(position).toString()
            if (position in village.indices) {
                selectedVillageCodeItem = villageCode[position]
                selectedbVillageLgdCodeItem = villageLgdCode[position]


            } else {
                Toast.makeText(requireContext(), "Invalid selection", Toast.LENGTH_SHORT).show()
            }
        }



        binding.spinnerProgramName.setOnItemClickListener { parent, view, position, id ->
            selectedprogramNameItem = parent.getItemAtPosition(position).toString()
            if (position in programName.indices) {
                selectedprogramNameCodeItem = programNameCode[position]


            } else {
                Toast.makeText(requireContext(), "Invalid Selection", Toast.LENGTH_SHORT).show()
            }
        }








        binding.image1.setOnClickListener {

            openCamera(binding.image1)
        }
        binding.image2.setOnClickListener {
            openCamera(binding.image2)
        }
    }


    private fun collectEapAutoFetchResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getEapAutoFetchListAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            toastShort(baseErrorResponse.message)
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getEapAutoFetchListAPI ->
                            if (getEapAutoFetchListAPI.responseCode == 200) {


                                for (x in getEapAutoFetchListAPI.wrappedList){

                                    orgCode= x.orgCode
                                    orgName= x.orgName
                                    instituteName= x.instituteName
                                    instituteCode= x.instituteCode
                                    officialName= x.officialName
                                    designationName= x.designation
                                    AppUtil.saveEapCanAgeLimitPreference(requireContext(),x.ageLimit)
                                    binding.tvOrganizationName.text=orgName
                                    binding.tvInstituteName.text=instituteName
                                    binding.tvparticipatingOfficialName.text=officialName
                                    binding.tvdesiginationName.text=designationName

                                }


                                stateAdapter.notifyDataSetChanged()
                            } else if (getEapAutoFetchListAPI.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }   else if (getEapAutoFetchListAPI.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getEapAutoFetchListAPI.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                }
            }
        }
    }
    private fun collectProgramNameResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getProgramListAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            toastShort(baseErrorResponse.message)
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getProgramListAPI ->
                            if (getProgramListAPI.responseCode == 200) {
                                programNameList = getProgramListAPI.wrappedList
                                programName.clear()
                                programNameCode.clear()

                                for (x in programNameList) {
                                    programName.add(x.programName)
                                    programNameCode.add(x.programCode)
                                }

                                stateAdapter.notifyDataSetChanged()
                            } else if (getProgramListAPI.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }  else if (getProgramListAPI.responseCode == 401) {
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())


                            }
                            else toastLong(getProgramListAPI.responseDesc)
                        } ?: showSnackBar("Internal Server Error")
                    }

                }
            }
        }
    }


    private fun collectInsertResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.insertEAPAPI) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")

                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { insertApiResp ->
                            if (insertApiResp.responseCode == 200) {

                                showSnackBar("Success")

                                findNavController().navigate(
                                    R.id.eapListFragment,
                                    null,
                                    NavOptions.Builder()
                                        .setPopUpTo(R.id.EAPAwarnessFormFragment, true)
                                        .build()
                                )



                            }


                            else if (insertApiResp.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }
                            else if (insertApiResp.responseCode==401){

                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(insertApiResp.responseDesc)
                            }

                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> { showSnackBar("Internal Server Error")}
                }
            }
        }
    }


    private fun collectStateResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getStateList) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getStateResponse ->
                            if (getStateResponse.responseCode == 200) {
                                stateList = getStateResponse.stateList
                                state.clear()
                                stateCode.clear()
                                stateLgdCode.clear()

                                for (x in stateList) {
                                    state.add(x.stateName)
                                    stateCode.add(x.stateCode)
                                    stateLgdCode.add(x.lgdStateCode)
                                }

                                stateAdapter.notifyDataSetChanged()
                            } else if (getStateResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }   else if (getStateResponse.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getStateResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> { showSnackBar("Internal Server Error")}
                }
            }
        }
    }
    private fun collectDistrictResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getDistrictList) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getDistrictResponse ->
                            if (getDistrictResponse.responseCode == 200) {
                                districtList = getDistrictResponse.districtList

                                districtLgdCode.clear()
                                district.clear()
                                districtCode.clear()

                                for (x in districtList) {
                                    district.add(x.districtName)
                                    districtCode.add(x.districtCode)
                                    districtLgdCode.add(x.lgdDistrictCode)
                                }





                                withContext(Dispatchers.Main) {
                                    districtAdapter.notifyDataSetChanged()

                                }
                            } else if (getDistrictResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }   else if (getDistrictResponse.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getDistrictResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> { showSnackBar("Internal Server Error")}
                }
            }
        }
    }
    private fun collectBlockResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getBlockList) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getBlockResponse ->
                            if (getBlockResponse.responseCode == 200) {
                                blockList = getBlockResponse.blockList
                                block.clear()
                                blockCode.clear()
                                blockLgdCode.clear()

                                for (x in blockList) {
                                    block.add(x.blockName)
                                    blockCode.add(x.blockCode) // Replace with actual field
                                    blockLgdCode.add(x.lgdBlockCode) // Replace with actual field
                                }
                                blockAdapter.notifyDataSetChanged()
                            } else if (getBlockResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }  else if (getBlockResponse.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getBlockResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> { showSnackBar("Internal Server Error")}
                }
            }
        }
    }
    private fun collectGpResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getGpList) {
                when (it) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        it.error?.let { baseErrorResponse ->
                            showSnackBar("Internal Server Error111")
                        }
                    }

                    is Resource.Success -> {
                        hideProgressBar()
                        it.data?.let { getGpResponse ->
                            if (getGpResponse.responseCode == 200) {
                                gpList = getGpResponse.grampanchayatList
                                gp.clear()
                                gpCode.clear()
                                gpLgdCode.clear()

                                for (x in gpList) {
                                    gp.add(x.gpName)
                                    gpCode.add(x.gpCode) // Replace with actual field
                                    gpLgdCode.add(x.lgdGpCode) // Replace with actual field
                                }
                                blockAdapter.notifyDataSetChanged()
                            } else if (getGpResponse.responseCode == 301) {
                                showSnackBar("Please Update from PlayStore")
                            }  else if (getGpResponse.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getGpResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> { showSnackBar("Internal Server Error")}
                }
            }
        }
    }
    private fun collectVillageResponse() {
        lifecycleScope.launch {
            collectLatestLifecycleFlow(commonViewModel.getVillageList) { result ->
                when (result) {
                    is Resource.Loading -> showProgressBar()
                    is Resource.Error -> {
                        hideProgressBar()
                        showSnackBar(result.error?.message ?: "Error fetching data")
                    }
                    is Resource.Success -> {
                        hideProgressBar()
                        result.data?.let { getVillageResponse ->
                            if (getVillageResponse.responseCode == 200) {
                                villageList = getVillageResponse.villageList

                                village.clear()
                                villageCode.clear()
                                villageLgdCode.clear()

                                for (x in villageList) {
                                    village.add(x.villageName)
                                    villageCode.add(x.villageCode)
                                    villageLgdCode.add(x.lgdVillageCode)
                                }

                                // ✅ Update UI after data is set
                                withContext(Dispatchers.Main) {
                                    villageAdapter.notifyDataSetChanged()
                                }
                            }  else if (getVillageResponse.responseCode==401){
                                AppUtil.showSessionExpiredDialog(findNavController(),requireContext())
                            }
                            else {
                                toastLong(getVillageResponse.responseDesc)
                            }
                        } ?: showSnackBar("Internal Server Error")
                    }

                    else -> { showSnackBar("Internal Server Error")}
                }
            }
        }
    }


    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()

        // Get today's date (milliseconds)
        val today = calendar.timeInMillis

        // Get the last day of the current month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endOfMonth = calendar.timeInMillis

        // Set constraints: From today to the last day of the month
        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(today)     // Restrict to today
            .setEnd(endOfMonth)  // Restrict to the last day of the current month
            .setValidator(DateValidatorPointForward.now()) // Only future dates including today

        // Create Material Date Picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a Date")
            .setSelection(today) // Default today
            .setCalendarConstraints(constraintsBuilder.build()) // Apply constraints
            .build()

        // Show Date Picker
        datePicker.show(parentFragmentManager, "DATE_PICKER")

        // Handle date selection
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = sdf.format(Date(selection))
            textView.text = formattedDate
            selectedDate = formattedDate
        }
    }    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissions.any {
                ContextCompat.checkSelfPermission(requireContext(), it) != android.content.pm.PackageManager.PERMISSION_GRANTED
            }) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 100)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        return try {
            val maxSize = 1024 // Resize to max 1024px width/height
            val width = bitmap.width
            val height = bitmap.height
            val scale = if (width > height) maxSize.toFloat() / width else maxSize.toFloat() / height

            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap // Return the original bitmap if compression fails
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // Increase quality to 90
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            Base64.encodeToString(byteArray, Base64.NO_WRAP) // Use NO_WRAP to avoid line breaks
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun openCamera(imageView: ImageView) {
        checkAndRequestPermissions()
        currentImageView = imageView

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Camera permission required!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap == null || currentImageView == null) {
                Toast.makeText(requireContext(), "Image capture failed!", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val compressedBitmap = compressBitmap(imageBitmap)
            currentImageView?.setImageBitmap(compressedBitmap)

            val base64Image = bitmapToBase64(compressedBitmap)

            when (currentImageView?.id) {
                R.id.image1 -> {
                    image1Base64 = base64Image
                }
                R.id.image2 -> {
                    image2Base64 = base64Image
                }
            }

            getCurrentLocation()
            binding.lllatLang.visible()
            binding.llAdress.visible()
        }
    }



    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude

                locationLatLang = "Lat: $latitude, Lng: $longitude"
                binding.location.text = locationLatLang

                // Fetch and update address
                getAddressFromLocation(latitude!!, longitude!!)
            } else {
                // If last known location is null, request a fresh location update
                requestNewLocation()
            }
        }.addOnFailureListener {
            binding.location.text = getString(R.string.location_not_found)
            binding.address.text = getString(R.string.address_not_found)
            Log.e("LocationError", "Failed to get location: ${it.message}")
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocation() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000 // Update every 5 sec
        ).apply {
            setWaitForAccurateLocation(true) // Ensures accuracy
            setMinUpdateIntervalMillis(2000) // Minimum update interval
            setMaxUpdateDelayMillis(10000) // Max delay
            setMaxUpdates(1) // Get only one update and stop
        }.build()

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult.lastLocation?.let { location ->
                    latitude = location.latitude
                    longitude = location.longitude

                    locationLatLang = "Lat: $latitude, Lng: $longitude"
                    binding.location.text = locationLatLang

                    // Fetch and update address
                    getAddressFromLocation(latitude!!, longitude!!)

                    fusedLocationClient.removeLocationUpdates(this) // Stop updates after getting location
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale("en", "IN"))
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: "Address not available"
                val city = address.locality ?: "Unknown City"
                val state = address.adminArea ?: "Unknown State"
                val pincode = address.postalCode ?: "No Pincode"
                val country = address.countryName ?: "Unknown Country"

                locationAddress = "$fullAddress, $city, $state, $pincode, $country"
                binding.address.text = locationAddress
            } else {
                binding.address.text = getString(R.string.address_not_found)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.address.text = getString(R.string.error_address)
        }
    }


    override fun onCandidateAdded(count: Int) {
        updateCandidateCount(count)    }

    @SuppressLint("SetTextI18n")
    fun updateCandidateCount(count: Int) {
        binding.candidteCount.text = "Candidates: $count"
        counts=count.toString()

    }
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
            // proceed with file/media access
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkAndRequestStoragePermissions() {
        if (!hasStoragePermission(requireContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                storagePermissionLauncher.launch(AppUtil.storagePermissions)
            } else {
                storagePermissionLauncher.launch(arrayOf(AppUtil.legacyStoragePermission))
            }
        } else {
            // Permissions already granted, continue your logic
        }
    }


    private fun checkAndPromptGPS() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS is already enabled
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingLauncher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

}