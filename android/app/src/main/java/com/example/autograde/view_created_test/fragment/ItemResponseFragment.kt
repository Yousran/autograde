package com.example.autograde.view_created_test.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autograde.data.api.response.QuestionsShow
import com.example.autograde.data.api.response.UserTestsItem
import com.example.autograde.databinding.FragmentCreatedTestBinding
import com.example.autograde.databinding.ItemListQuestionCreatedTestBinding
import com.example.autograde.user_test.UserTestActivity
import com.example.autograde.view_created_test.CreatedTestActivity
import com.example.autograde.view_created_test.CreatedTestViewModel

class ItemResponseFragment : Fragment() {

    private var _binding: FragmentCreatedTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var createdTestViewModel: CreatedTestViewModel

    private lateinit var adapter: ItemResponseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatedTestBinding.inflate(layoutInflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createdTestViewModel = ViewModelProvider(requireActivity()).get(CreatedTestViewModel::class.java)


        // Set up RecyclerView only once
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFragment.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFragment.addItemDecoration(itemDecoration)


        adapter = ItemResponseAdapter { user ->
            val intent = Intent(requireActivity(), UserTestActivity::class.java)
            intent.putExtra("USER_TEST_ID", user.id)
            startActivity(intent)
        }
        binding.rvFragment.adapter = adapter

        observeItemQuestionById()
    }

    private fun observeItemQuestionById() {
        createdTestViewModel.userTestResponse.observe(viewLifecycleOwner) { user ->
            if (user.isNullOrEmpty()) {
                Log.d("ItemQuestionFragment", "Data kosong diterima!")
            } else {
                Log.d("ItemQuestionFragment", "Data diterima dari ViewModel: ${user.size} items")
                setReviewData(user)
            }
        }
    }


    private fun setReviewData(user: List<UserTestsItem>) {
        adapter.submitList(user)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
