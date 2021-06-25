package com.example.thebirthdaysapp.ui.main

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thebirthdaysapp.R
import com.example.thebirthdaysapp.helpers.Resource
import com.example.thebirthdaysapp.viewmodels.MainViewModel
import com.example.thebirthdaysapp.api.Result

class ListOfResultsFragment : Fragment() {

    companion object {
        fun newInstance() = ListOfResultsFragment()
    }

    val viewModel: MainViewModel by activityViewModels() // as data is shared between the fragments, this is scoped to the activity
    private lateinit var theRecyclerView: RecyclerView
    private lateinit var customAdapterForRecyclerView: CustomAdapterForRecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.list_of_results_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        theRecyclerView = view.findViewById(R.id.recyclerViewBirthdays)

        viewModel.getBirthdaysAndOtherData()

        val observer = Observer<Resource<List<Result>>> { birthdaysResource ->
            when (birthdaysResource) {
                is Resource.Success -> { // data won't be null if it gets here. Error conditions have been checked and guarded against in the repository.
                    customAdapterForRecyclerView = CustomAdapterForRecyclerView(birthdaysResource.data!!)
                    initializeRecyclerViewer()

                }
                is Resource.Error -> {

                    Toast.makeText(context, birthdaysResource.message, Toast.LENGTH_SHORT).show()

                }
                is Resource.Loading -> {

                    Toast.makeText(context, "Loading...please wait", Toast.LENGTH_SHORT).show()

                }
            }
        }
        viewModel.birthdayResultsResource.observe(viewLifecycleOwner, observer)

    }

    private fun initializeRecyclerViewer() {

        theRecyclerView.apply {
            adapter = customAdapterForRecyclerView
            layoutManager = LinearLayoutManager(requireContext())
        }

        val listener =  CustomAdapterForRecyclerView.OnItemClickListener { listIndexInDataSetOfItemTappedOn ->
                val action = ListOfResultsFragmentDirections.actionListOfResultsFragmentToDetailsFragment(listIndexInDataSetOfItemTappedOn)
                view?.findNavController()?.navigate(action)
            }

        customAdapterForRecyclerView.setOnItemClickListener(listener)

    }

}