package com.example.voicenotes.presentation.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.voicenotes.vk.VKWallPostCommand
import com.example.voicenotes.vk.VKServerUploadInfo2
import com.example.voicenotes.R
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityNotesBinding
import com.example.voicenotes.presentation.recycler.NoteListAdapter
import com.example.voicenotes.presentation.vm.NoteListViewModel
import com.example.voicenotes.presentation.vm.ViewModelFactory
import com.example.voicenotes.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import java.io.File
import javax.inject.Inject

class NotesActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val authLauncher = VK.login(this as ComponentActivity) { result : VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {}
            is VKAuthenticationResult.Failed -> {}
        }
    }

    private val binding by lazy {
        ActivityNotesBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as NoteListApp).component
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[NoteListViewModel::class.java]
    }

    private val deleteMsg by lazy { resources.getString(R.string.msg_asking_to_delete) }
    private val deleteBtnMsg by lazy { resources.getString(R.string.delete) }
    private lateinit var noteListAdapter: NoteListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        searchNoteAction()
        setupActionBar()
        setupButtonsAction()
        setupBottomSheet()
        setupSwipeListener(binding.rcView)
    }

    private fun setupSwipeListener(rvNoteList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder = AlertDialog.Builder(this@NotesActivity)
                builder.setTitle(deleteMsg)
                builder.setPositiveButton(deleteBtnMsg) {_, _ ->
                    val item = noteListAdapter.currentList[viewHolder.layoutPosition]
                    viewModel.deleteNote(item)
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvNoteList)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupButtonsAction() = with(binding) {
        buttonClose.setOnClickListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            buttonsBar.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        buttonAd.setOnClickListener {
            startActivity(Intent(this@NotesActivity, MainActivity::class.java))
        }

        buttonAuthScreen.setOnClickListener {
            val intent = AuthorizationActivity.newIntentOpenNotesActivity(
                this@NotesActivity,
                true
            )
            startActivity(intent)
        }


    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun searchNoteAction() {
        binding.editTextSearchNote.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchInDb(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchInDb(query: String) {
        viewModel.searchInDataBase("%$query%")
        viewModel.searchedQueryList.observe(this) {
            noteListAdapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        with(binding.rcView) {
            noteListAdapter = NoteListAdapter()
            adapter = noteListAdapter
        }
        viewModel.noteList.observe(this) {
            noteListAdapter.submitList(it)
        }
        setupClickListener()
    }

    private fun setupClickListener() = with(noteListAdapter) {
        onNoteItemClickListener = {
            val intent = PlayerActivity.newIntentStartPlayer(
                this@NotesActivity,
                it.filepath,
                it.fileName,
                it.timesTamp
            )
            startActivity(intent)
        }

        onNoteItemLongClickListener = {
            if (VK.isLoggedIn()) {
                val noteToShareFile = File(it.filepath)
                val file = Uri.fromFile(noteToShareFile)
                VK.execute(
                    VKWallPostCommand(
                        "kkkkkkkk",
                        file,
                        it.fileName,
                        98
                    ), object:
                        VKApiCallback<VKServerUploadInfo2> {
                        override fun fail(error: Exception) {
                            showToast(error.toString())
                            println(error.toString())
                        }
                        override fun success(result: VKServerUploadInfo2) {
                            showToast(result.uploadUrl)
                        }
                    })

            } else {
                val builder = AlertDialog.Builder(this@NotesActivity)
                builder.setTitle("Ошибка")
                builder.setMessage("Для загрузки в документы вы должны авторизоваться через VK")
                builder.setPositiveButton("Авторизоваться") {_, _ ->
                    authLauncher.launch(arrayListOf(VKScope.DOCS))
                }
                builder.setNegativeButton("отмена") { _, _ -> }
                val dialog = builder.create()
                dialog.show()

            }


        }

    }

    companion object {
        fun newIntentOpenNotesActivity(context: Context) = Intent(context, NotesActivity::class.java)
    }
}