import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyekstarling.R
import com.example.proyekstarling.TransactionAdapter
import com.example.proyekstarling.dashboardowner
import com.example.proyekstarling.transaksi
import com.google.firebase.database.*
import com.example.proyekstarling.databinding.FraglayananownerBinding
import com.example.proyekstarling.fragaboutownerqr
import com.example.proyekstarling.fragaboutownervideo

class fraglayananowner : Fragment() {
    lateinit var binding: FraglayananownerBinding
    lateinit var thisParent: dashboardowner
    private lateinit var database: DatabaseReference
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList = mutableListOf<Pair<String, transaksi>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FraglayananownerBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnqr.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragaboutownerqr())
                .addToBackStack(null)
                .commit()
        }

        binding.btnvideo.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragaboutownervideo())
                .addToBackStack(null)
                .commit()
        }
        return view
    }
}