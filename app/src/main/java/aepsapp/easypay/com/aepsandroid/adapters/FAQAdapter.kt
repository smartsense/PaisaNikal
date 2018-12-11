package aepsapp.easypay.com.aepsandroid.adapters

import aepsapp.easypay.com.aepsandroid.R
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * Created by Viral on 19-06-2017.
 */

class FAQAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater: LayoutInflater
    private val questions: Array<String>
    private val answers: Array<String>

    init {
        inflater = LayoutInflater.from(context)
        questions = context.resources.getStringArray(R.array.faq_que)
        answers = context.resources.getStringArray(R.array.faq_ans)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FAQHolder(inflater.inflate(R.layout.faq_item, parent, false))
    }

    override fun onBindViewHolder(rholder: RecyclerView.ViewHolder, position: Int) {
        val holder = rholder as FAQHolder

        val question = questions[position]
        val answer = answers[position]

        holder.txtAnswer.visibility = View.GONE
        holder.txtAnswer.text = answer
        holder.txtQuestion.text = question

        holder.txtQuestion.setOnClickListener {
            if (holder.txtAnswer.visibility == View.GONE)
                holder.txtAnswer.visibility = View.VISIBLE
            else
                holder.txtAnswer.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        return questions.size
    }


    private inner class FAQHolder(internal var baseView: View) : RecyclerView.ViewHolder(baseView) {
        internal var txtQuestion: TextView
        internal var txtAnswer: TextView

        init {
            txtQuestion = baseView.findViewById(R.id.faq_txtquestion) as TextView
            txtAnswer = baseView.findViewById(R.id.faq_txtanswer) as TextView
        }
    }
}
