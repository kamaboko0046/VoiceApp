package com.kamaboko.voiceapp.manege

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kamaboko.voiceapp.database.Voice
import com.kamaboko.voiceapp.databinding.ListItemVoiceBinding
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class ManageVoiceAdapter(val clickListener: (voice: Voice) -> Unit) : ListAdapter<Voice, ManageVoiceAdapter.ViewHolder>(VoiceDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemVoiceBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Voice, clickListener: (voice: Voice) -> Unit) {
            // 日付フォーマット
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE)
            binding.voice = item
            binding.CreateText.text = sdf.format(item.createTimeMilli)
            binding.imageView.setOnClickListener {
                clickListener(item)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemVoiceBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class VoiceDiffCallback : DiffUtil.ItemCallback<Voice>() {

    override fun areItemsTheSame(oldItem: Voice, newItem: Voice): Boolean {
        return oldItem.voiceId == newItem.voiceId
    }

    override fun areContentsTheSame(oldItem: Voice, newItem: Voice): Boolean {
        return oldItem == newItem
    }
}

class VoiceListener(val clickListener: (voiceId: Long) -> Unit) {
    fun onClick(voice: Voice) = clickListener(voice.voiceId)
}
