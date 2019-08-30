package me.martichou.be.grateful.ui.add

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.mapboxsdk.Mapbox
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentAddmainBinding
import me.martichou.be.grateful.di.Injectable
import me.martichou.be.grateful.util.CompressImage
import me.martichou.be.grateful.util.PlacePicker
import me.martichou.be.grateful.vo.Notes
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

open class AddMainFragment : BottomSheetDialogFragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var addViewModel: AddViewModel
    private lateinit var binding: FragmentAddmainBinding

    private lateinit var c: Context
    private lateinit var storageDir: File
    private val placePicker = 548

    override fun onAttach(context: Context) {
        c = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddmainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addViewModel = ViewModelProvider(this, viewModelFactory).get(AddViewModel::class.java)
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.hdl = this
        // Storage dir
        storageDir = c.getDir("imgForNotes", Context.MODE_PRIVATE)
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onDismiss(dialog: DialogInterface) {
        Timber.d("Called")
        Timber.d("Idk ${addViewModel.hasBeenSaved}")
        Timber.d("Idk2 ${addViewModel.hasPhoto}")
        if (!addViewModel.hasBeenSaved && addViewModel.hasPhoto) {
            Timber.d("Passed into")
            CoroutineScope(Dispatchers.IO).launch {
                val imageFile = File(storageDir, addViewModel.randomImageName)
                Timber.d("Into into ${imageFile.absolutePath}")
                if (imageFile.exists()) {
                    Timber.d("Second into")
                    val deleted = imageFile.delete()
                    Timber.d("Deleting")
                    if (!deleted) {
                        Timber.e("Cannot delete the file..")
                    } else {
                        Timber.d("Succes. The file has been deleted")
                    }
                }
            }
        }
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog = BottomSheetDialog(requireContext(), theme)

    /**
     * Open the image selector
     */
    fun openImageSelector(v: View) {
        val metrics = requireContext().resources!!.displayMetrics
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(metrics.widthPixels, metrics.heightPixels / 2)
                .setAllowRotation(true)
                .start(requireContext(), this)
    }

    /**
     * Open the place selector
     */
    fun openMapsSelector(v: View) {
        Mapbox.getAccessToken()?.let {
            startActivityForResult(
                    PlacePicker.IntentBuilder()
                            .accessToken(it)
                            .build(requireActivity()), placePicker)
        }
    }

    /**
     * Open the place selector
     */
    fun openDateSelector(v: View) {
        val calendar = Calendar.getInstance()
        val c = DatePickerDialog(requireContext(), R.style.DialogTheme, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            addViewModel.dateSelected = cal
            binding.addDateBtnBs.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_roundaccent)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        c.datePicker.maxDate = calendar.timeInMillis
        c.show()
    }

    /**
     * Callback for the Matisse call
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == AppCompatActivity.RESULT_OK)
                    CompressImage(requireContext(), addViewModel, File(CropImage.getActivityResult(data).uri.path), binding.addPhotoBtnBs)
                else {
                    Toast.makeText(context, resources.getString(R.string.set_image), Toast.LENGTH_SHORT).show()
                    return
                }
            }
            placePicker -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val carmenFeature = PlacePicker.getPlace(data)
                    if (carmenFeature?.context() == null) {
                        Toast.makeText(context, "Cannot retreive place name, try again", Toast.LENGTH_LONG).show()
                    } else {
                        var city: String? = null
                        var country: String? = null
                        Timber.d("Localisation is ${carmenFeature.toJson()}")
                        carmenFeature.context()!!.forEach {
                            Timber.d("Carmen Localisation $it")
                            when {
                                it.id()!!.contains("locality", false) -> city = it.text()
                                it.id()!!.contains("place", false) && city == null -> city = it.text()
                                it.id()!!.contains("country", false) -> country = it.text()
                            }
                        }

                        if (country != null) {
                            if (city == null) {
                                addViewModel.placeCity = "$country"
                                Toast.makeText(context, "$country", Toast.LENGTH_LONG).show()
                            } else {
                                addViewModel.placeCity = "$city, $country"
                                Toast.makeText(context, "$city, $country", Toast.LENGTH_LONG).show()
                            }
                            binding.addLocBtnBs.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_roundaccent)
                        } else {
                            Toast.makeText(context, "Cannot retreive place name, try again", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Close this fragment and save info
     */
    fun btnSaveAction(v: View) {
        if (!addViewModel.isWorking && addViewModel.hasPhoto) {
            val titleOfTheNote: String = binding.addTitleNoteBs.text.toString()
            if (titleOfTheNote.isNotEmpty()) run {
                addViewModel.hasBeenSaved = true
                addViewModel.insertNote(
                        Notes(
                                titleOfTheNote,
                                binding.addContentNoteBs.text.toString(),
                                addViewModel.randomImageName,
                                addViewModel.dateDefaultOrNot(),
                                addViewModel.dateOrNot(),
                                addViewModel.locOrNot()
                        )
                )
                dismiss()
            } else {
                Toast.makeText(context, resources.getString(R.string.enter_title), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, resources.getString(R.string.set_image), Toast.LENGTH_SHORT).show()
        }
    }
}