package com.example.ayamjumpa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.R
import com.example.ayamjumpa.adapter.ItemDetailAdapter
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.databinding.FragmentDetailPesananBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class FragmentDetailPesanan : Fragment() {
    private lateinit var binding: FragmentDetailPesananBinding
    private lateinit var pesanan: Pesanan
private val args :FragmentDetailPesananArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailPesananBinding.inflate(inflater, container, false)

        safeArgs()
        setView()
        setupRecylerView()


        return binding.root
    }


    private fun safeArgs() {

        pesanan = args.pesanan!!

        binding.pesanan = pesanan

    }

    private fun setView() {
        binding.detailAlamat.text =
            getString(R.string.alamat, pesanan.alamat?.alamat, pesanan.alamat?.keterangan)
        binding.detailTanggal.text = formatTanggal()
        binding.detailHarga.text = formatRupiah(pesanan.totalHarga?.minus(pesanan.alamat?.ongkir!!)!!)
        binding.detailOngkir.text = formatRupiah(pesanan.alamat?.ongkir!! )
        binding.detailTotalsemua.text = formatRupiah(pesanan.totalHarga!!)
        binding.detailCatatan.text = getString(R.string.catatan, pesanan.catatan)
    }

    private fun formatTanggal(): String {

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault())
        val date = pesanan.tanggal.toDate()

        return simpleDateFormat.format(date)
    }

    private fun setupRecylerView() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val itemDetailAdapter = ItemDetailAdapter(requireContext())

        itemDetailAdapter.differ.submitList(pesanan.item)

        binding.detailRecyclerView.layoutManager = linearLayoutManager
        binding.detailRecyclerView.adapter = itemDetailAdapter


    }
    private fun formatRupiah(number: Double): String {


        val localeID = Locale("in", "ID")

        val format = NumberFormat.getCurrencyInstance(localeID)

        return format.format(number)


    }


}