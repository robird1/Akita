package com.ulsee.ulti_a100.ui.people

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ulsee.ulti_a100.MainActivity
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.data.response.AllPerson
import com.ulsee.ulti_a100.databinding.FragmentPeopleBinding

private val TAG = PeopleFragment::class.java.simpleName
private const val REQUEST_ACTIVITY_EDITOR = 1234

class PeopleFragment : Fragment(), RecyclerItemTouchHelper.ItemTouchListener,
    MenuItem.OnActionExpandListener, ActionMode.Callback, PeopleAdapter.OnRecyclerItemCallbackListener {

    private lateinit var binding: FragmentPeopleBinding
    private lateinit var adapter: PeopleAdapter
    private lateinit var viewModel: PeopleViewModel
    private var position = -1
    private var isSearchMode = false
//    private lateinit var searchView: PeopleSearchView
    private var actionMode: ActionMode? = null
    private lateinit var fileList: List<AllPerson>              // for action mode
    private lateinit var actionModeView: RelativeLayout
    private lateinit var actionModeTitle: TextView
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val args: PeopleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPeopleBinding.inflate(inflater, container, false)
        initViewModel()
        initSwipeRefreshView()
        initRecyclerView()
        initRecyclerViewTouchHelper()
        initFab()
//        setHasOptionsMenu(true)
        initActionModeView()
        setFragmentTitle()
        observePeopleList()
        observeDeletePeople()
//        observeSearchPeople()

        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, PeopleFactory(PeopleRepository(args.url)))
            .get(PeopleViewModel::class.java)
        binding.progressView.visibility = View.VISIBLE
    }

    private fun initSwipeRefreshView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadRecord()
        }
    }

    private fun initRecyclerView() {
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)
        adapter = PeopleAdapter(viewModel)
        binding.recyclerView.adapter = adapter
        adapter.setOnRecyclerItemCallbackListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ACTIVITY_EDITOR) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.loadRecord()

//                if (data?.getStringExtra("mode") == "add") {
//                    (binding.recyclerView.adapter as PeopleAdapter).addItem()
//
//                } else if (data?.getStringExtra("mode") == "edit") {
//                    (binding.recyclerView.adapter as PeopleAdapter).editItem(position)
//
//                } else {
//
//                }

//                Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()

                // close searchView after edit a record
                (activity as MainActivity).binding.toolbar.collapseActionView()
            } else {
                // do nothing. user clicks back button.
            }

            closeProgressView()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
////        Log.d(TAG, "[Enter] onCreateOptionsMenu")
//        inflater.inflate(R.menu.people_option_menu, menu)
//        configSearchView(menu)
////        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onDestroyOptionsMenu() {
//        super.onDestroyOptionsMenu()
//        if (actionMode != null) {
//            actionMode!!.finish()
//            actionMode = null
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
////            R.id.select_file_to_sync -> {
////                startActionMode()
////                return true
////            }
//            R.id.sync -> {
//                showConfirmDialog()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun showConfirmDialog() {
//        val ctx = requireContext()
//        AlertDialog.Builder(ctx)
//            .setMessage(ctx.getString(R.string.confirm_face_sync))
//            .setPositiveButton(
//                ctx.getString(R.string.sync)
//            ) { _, _ ->
//                //                        viewModel.deleteDevice(deviceID)
//                viewModel.synFace()
//            }
//            .setNegativeButton(
//                ctx.getString(R.string.cancel)
//            ) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }
//
//    override fun onQueryTextSubmit(query: String?): Boolean {
//        if (query.isNullOrEmpty()) return true
//        searchView.clearFocus()
//        viewModel.searchPeople(query)
//        return false
//    }
//
//    override fun onQueryTextChange(newText: String?): Boolean {
//        return false
//    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        isSearchMode = true
        binding.fab.visibility = View.INVISIBLE
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        isSearchMode = false
        viewModel.loadRecord()
        binding.fab.visibility = View.VISIBLE
        return true
    }

    override fun onRecyclerItemClick(position: Int, data: AllPerson) {
        this.position = position
        if (actionMode == null) {
            openEditor(data, true)
        } else {
            selectAtPosition(position)
        }
    }

//    override fun onRecyclerItemLongClick(position: Int) {
//        if (actionMode == null) {
//            startActionMode()
//            selectAtPosition(position)
//        }
//    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        initView(mode)
        initMenu(menu)
        updateActionModeTitle(0)
//        toggleActionModeAction(0)
        toggleFabSelectAll(false)
//        toast(R.string.edit_mode)
        toggleItemTouchHelper(null)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val isEmpty = getSelectedCount() == 0
        val id = item.itemId
        if (isEmpty) {
//            toast(R.string.no_item_selected)
            Toast.makeText(
                requireContext(),
                getString(R.string.no_item_selected),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            when (id) {
                // TODO

            }
        }
        return false
    }

    // TODO enableRecyclerRefresh
    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        clearAllSelection()
        enableFabEdit(true)
//        enableRecyclerRefresh(true)
        toggleItemTouchHelper(binding.recyclerView)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        val deletedItem= (binding.recyclerView.adapter as PeopleAdapter).peopleList[position]
        deletePeople(deletedItem, position)
    }

    private fun initRecyclerViewTouchHelper() {
        itemTouchHelper = ItemTouchHelper(getItemTouchCallback())
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun getItemTouchCallback(): ItemTouchHelper.SimpleCallback {
        return RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, this)
    }

    private fun toggleItemTouchHelper(view: RecyclerView?) {
        itemTouchHelper.attachToRecyclerView(view)
    }

    private fun initActionModeView() {
        actionModeView =
            LayoutInflater.from(requireContext()).inflate(R.layout.action_mode_custom, null) as RelativeLayout
        actionModeTitle =
            actionModeView.findViewById<View>(R.id.action_mode_custom_title) as TextView
    }

    private fun startActionMode() {
        if (actionMode == null)
            requireActivity().startActionMode(this)
    }

    private fun closeEditorMode() {
        actionMode?.finish()
    }

    // TODO enableRecyclerRefresh
    private fun initView(mode: ActionMode) {
        actionMode = mode
        actionMode!!.customView = actionModeView
//        enableRecyclerRefresh(false)
    }

    private fun initMenu(menu: Menu) {
        requireActivity().menuInflater.inflate(R.menu.people_action_mode_menu, menu)
    }


    private fun updateActionModeTitle(count: Int) {
        val format =
            resources.getString(if (count <= 1) R.string.msg_file_selected else R.string.msg_files_selected)
        actionModeTitle.text = String.format(format, count)
    }


//    // TODO check this function's purpose
//    private fun toggleActionModeAction(count: Int) {
//        val visible = count == 1
//        if (visible) {
//            val files: List<People> = getSelectedFiles()
//        }
////        mEditorMode.getMenu().findItem(R.id.file_manage_editor_action_rename).setVisible(visible)
//    }

    private fun toggleFabSelectAll(selectAll: Boolean) {
        val resId: Int =
            if (selectAll) R.drawable.ic_floating_unselectall_white else R.drawable.ic_floating_selectall_white
        binding.fab.setImageResource(resId)
        binding.fab.visibility = View.VISIBLE
    }

    private fun clearAllSelection() {
        for (file in fileList) file.checked = false
        adapter.notifyDataSetChanged()
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            if (actionMode == null) {
                openEditor(null, false)
            } else
                toggleSelectAll()
        }
    }

    private fun enableFabEdit(enabled: Boolean) {
        binding.fab.setImageResource(R.drawable.icon_add)
        binding.fab.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
    }

//    private fun enableRecyclerRefresh(enable: Boolean) {
//        mRecyclerRefresh.setEnabled(enable)
//    }

    private fun selectAtPosition(position: Int) {
        val checked: Boolean = fileList[position].checked
        fileList[position].checked = !checked
        adapter.notifyItemChanged(position)
        val count: Int = getSelectedCount()
        val selectAll = count == fileList.size
        updateActionModeTitle(count)
//        toggleActionModeAction(count)
        toggleFabSelectAll(selectAll)
    }

    private fun toggleSelectAll() {
        var count = getSelectedCount()
        var selectAll = count != 0 && count == fileList.size
        if (selectAll) clearAllSelection() else checkAllSelection()
        selectAll = !selectAll
        count = if (selectAll) fileList.size else 0
        updateActionModeTitle(count)
//        toggleActionModeAction(count)
        toggleFabSelectAll(selectAll)
    }

    private fun checkAllSelection() {
        for (file in fileList) file.checked = true
        adapter.notifyDataSetChanged()
    }

//    private fun configSearchView(menu: Menu) {
//        val menuSearchItem = menu.findItem(R.id.my_search)
//        searchView = (menuSearchItem.actionView as PeopleSearchView)
//        searchView.queryHint = "search name"
//        searchView.setOnQueryTextListener(this)
//        menuSearchItem.setOnActionExpandListener(this)
//    }

    private fun getSelectedCount(): Int {
        var count = 0
        for (file in fileList) {
            if (file.checked) count++
        }
        return count
    }

    private fun getSelectedFiles(): List<AllPerson> {
        val files = ArrayList<AllPerson>()
        for (file in fileList) {
            if (file.checked) files.add(file)
        }
        return files
    }

    private fun observePeopleList() {
        viewModel.peopleList.observe(viewLifecycleOwner, { list ->
            closeProgressView()
            if (list != null) {
                Log.d(TAG, "[Enter] observePeopleList size: ${list.size}")
                fileList = list
                (binding.recyclerView.adapter as PeopleAdapter).setList(list)

            } else {
                if (viewModel.errorCode == -1) {    // empty list
                    (binding.recyclerView.adapter as PeopleAdapter).clearList()
                } else {
                    Toast.makeText(requireContext(), "Error(${viewModel.errorCode})", Toast.LENGTH_SHORT).show()
                    viewModel.resetErrorCode()
                }
            }
        })
    }

    private fun observeDeletePeople() {
        viewModel.result.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { isSuccess ->
                if (isSuccess) {
                    Log.d(TAG, "[Enter] observeDeletePeople() success!!")
                    (binding.recyclerView.adapter as PeopleAdapter).removeItem(position)
                    Toast.makeText(requireContext(), getString(R.string.remove_successfully), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error(${viewModel.errorCode})", Toast.LENGTH_SHORT).show()
                }
                closeProgressView()
            }
        })
    }

//    private fun observeSearchPeople() {
//        viewModel.searchPeopleList.observe(viewLifecycleOwner, { it ->
//            it.getContentIfNotHandled()?.let {
//                Log.d(TAG, "[Enter] observeSearchPeople size: ${it.size}")
//                (binding.recyclerView.adapter as PeopleAdapter).setList(it)
//                fileList = it
//            }
//        })
//    }

    private fun openEditor(people: AllPerson?, isEditMode: Boolean) {
        val intent = Intent(context, EditorActivity::class.java)
        if (isEditMode) {
            // TODO refactor
            AttributeType.NAME.isInputValid = true
            AttributeType.WORK_ID.isInputValid = true
            AttributeType.GENDER.isInputValid  = true
            AttributeType.MAIL.isInputValid = true
        }
        if (people != null) {    // edit mode
            AttributeType.setAttributeData(people)
        } else {   // add mode
            AttributeType.clearAttributeData()
        }
        intent.putExtra("is_edit_mode", isEditMode)
        intent.putExtra("url", args.url)
        startActivityForResult(intent, REQUEST_ACTIVITY_EDITOR)
    }

    private fun deletePeople(people: AllPerson, position: Int) {
        this.position = position
        binding.progressView.visibility = View.VISIBLE
        viewModel.deletePerson(people)
    }

    private fun setFragmentTitle() {
        (activity as MainActivity).setTitle("People")
    }

    private fun closeProgressView() {
        binding.progressView.visibility = View.INVISIBLE
        binding.swipeRefreshLayout.isRefreshing = false
    }

}


class PeopleSearchView(context: Context) : SearchView(context){
    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            if (!isIconified) {
                onActionViewCollapsed()
            }
        }
        return super.dispatchKeyEventPreIme(event)
    }


}