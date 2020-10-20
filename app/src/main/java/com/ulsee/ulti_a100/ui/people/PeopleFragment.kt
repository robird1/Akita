package com.ulsee.ulti_a100.ui.people

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.databinding.FragmentPeopleBinding
import com.ulsee.ulti_a100.model.People

private val TAG = PeopleFragment::class.java.simpleName
private const val REQUEST_ACTIVITY_EDITOR = 1234

class PeopleFragment : Fragment(), RecyclerItemTouchHelper.ItemTouchListener {
    private lateinit var binding: FragmentPeopleBinding
    private val adapter = PeopleAdapter(this)
    private lateinit var viewModel: PeopleViewModel
    private var mPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPeopleBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, PeopleFactory(PeopleRepository()))
            .get(PeopleViewModel::class.java)

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.fab.setOnClickListener {
            openEditor(People(), false)
        }

        ItemTouchHelper(getItemTouchCallback()).attachToRecyclerView(binding.recyclerView)

        (activity as MainActivity).setTitle("People")

        observePeopleList()
        observeDeletePeople()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ACTIVITY_EDITOR) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.loadRecord()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun observePeopleList() {
        viewModel.peopleList.observe(viewLifecycleOwner, {
            Log.d(TAG, "[Enter] observePeopleList size: ${it.size}")
            (binding.recyclerView.adapter as PeopleAdapter).setList(it)
        })
    }

    private fun observeDeletePeople() {
        viewModel.result.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { isSuccess ->
                if (isSuccess) {
                    Log.d(TAG, "[Enter] observeDeletePeople() success!!")
                    (binding.recyclerView.adapter as PeopleAdapter).removeItem(mPosition)
                    Toast.makeText(requireContext(), getString(R.string.remove_successfully), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun openEditor(people: People, isEditMode: Boolean) {
        val intent = Intent(context, EditorActivity::class.java)
        if (isEditMode) {
            // TODO refactor
            AttributeType.NAME.isInputValid = true
            AttributeType.WORK_ID.isInputValid = true
            AttributeType.GENDER.isInputValid  = true
            AttributeType.MAIL.isInputValid = true
        }
        AttributeType.setAttributeData(people)
        intent.putExtra("is_edit_mode", isEditMode)
        startActivityForResult(intent, REQUEST_ACTIVITY_EDITOR)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        val deletedItem: People = (binding.recyclerView.adapter as PeopleAdapter).peopleList[position]
        deletePeople(deletedItem, position)
    }

    private fun getItemTouchCallback(): ItemTouchHelper.SimpleCallback {
        return RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, this)
    }

    private fun deletePeople(people: People, position: Int) {
        mPosition = position
        viewModel.deleteRecord(people)
    }

}