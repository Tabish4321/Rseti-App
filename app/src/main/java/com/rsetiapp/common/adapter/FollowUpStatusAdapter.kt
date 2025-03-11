import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rsetiapp.common.model.response.FollowUpStatus
import com.rsetiapp.databinding.ItemFollowUpStatusBinding

class FollowUpStatusAdapter(
    private val followUpStatusList: List<FollowUpStatus>
) : RecyclerView.Adapter<FollowUpStatusAdapter.FollowUpStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUpStatusViewHolder {
        val binding =
            ItemFollowUpStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowUpStatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowUpStatusViewHolder, position: Int) {
        val followUpStatus = followUpStatusList[position]
        holder.bind(followUpStatus)
    }

    override fun getItemCount(): Int = followUpStatusList.size

    inner class FollowUpStatusViewHolder(private val binding: ItemFollowUpStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(followUpStatus: FollowUpStatus) {
            binding.tvFollowUpStatus.text = followUpStatus.type

            binding.followUpStatus.isChecked =
                when (followUpStatus.status) {
                    "Settled" -> true
                    "Not Settled" -> false
                    else -> false
                }
        }
    }
}
