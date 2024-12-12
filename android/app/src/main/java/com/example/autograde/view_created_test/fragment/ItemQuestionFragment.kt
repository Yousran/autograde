package com.example.autograde.view_created_test.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autograde.data.api.response.QuestionsShow
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.FragmentCreatedTestBinding
import com.example.autograde.view_created_test.CreatedTestViewModel


class ItemQuestionFragment : Fragment() {

    private var _binding: FragmentCreatedTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var createdTestViewModel: CreatedTestViewModel

    private lateinit var adapter: ItemQuestionAdapter

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

        // Initialize Adapter only once
        adapter = ItemQuestionAdapter { question ->
            // Handle item click if needed
        }
        binding.rvFragment.adapter = adapter

        observeItemQuestionById()
    }

    private fun observeItemQuestionById() {
        createdTestViewModel.testQuestionItemResponse.observe(viewLifecycleOwner) { test ->
            if (test.isNullOrEmpty()) {
                Log.d("ItemQuestionFragment", "Data kosong diterima!")
            } else {
                Log.d("ItemQuestionFragment", "Data diterima dari ViewModel: ${test.size} items")
                setReviewData(test)
            }
        }
    }


    private fun setReviewData(question: List<QuestionsShow>) {
        // Update adapter data
        adapter.submitList(question)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
