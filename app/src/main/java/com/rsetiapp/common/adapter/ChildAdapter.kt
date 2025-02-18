import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.common.fragments.HomeFragmentDirections
import com.rsetiapp.common.model.response.Form
import com.rsetiapp.databinding.ItemChildBinding

class ChildAdapter(
    private val formList: List<Form>
) : RecyclerView.Adapter<ChildAdapter.FormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val binding = ItemChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val form = formList[position]
        holder.bind(form)
    }

    override fun getItemCount(): Int = formList.size

    inner class FormViewHolder(private val binding: ItemChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(form: Form) {
            binding.tvFormName.text = form.formName

            // Click to navigate to another fragment
            binding.root.setOnClickListener {
                if (form.formCd.equals("ADD_EAP")) {
                    val action = HomeFragmentDirections.actionHomeFrahmentToEAPAwarnessFormFragment(
                        form.formName,
                        form.formCd
                    )
                    binding.root.findNavController().navigate(action)
                }
            }
        }
    }
}
