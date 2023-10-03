package ru.music.radiostationvedaradio.ui.screens

import androidx.fragment.app.Fragment


class ViewPagerFragment : Fragment() /*PageFragment.OnFragmentReadyListener*/ {
//
//    companion object {
//        @JvmStatic
//        fun newInstance() {
//        }
//    }
//
//    private lateinit var parentActivity: MainActivity
//    private lateinit var reader: Reader
//    private lateinit var viewPager: ViewPager
//    private lateinit var mSectionPagerAdapter: SectionsPagerAdapter
//    private var pxScreenWidth: Int = 0
//    private var pageCount = Int.MAX_VALUE
//
//    private var isPickedWebView = false
//    private var isSkippedToPage = false
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view0 = inflater.inflate(R.layout.fragment_view_pager, container, false)
//        parentActivity = activity as MainActivity
//        overrideOnBackPressedWithCallback()
//
//
//        pxScreenWidth = resources.displayMetrics.widthPixels
//        mSectionPagerAdapter = SectionsPagerAdapter(parentActivity.supportFragmentManager)
//
//        viewPager = view0.view_pager_container
//        viewPager.offscreenPageLimit = 0
//        viewPager.adapter = mSectionPagerAdapter
//
//
//        if (parentActivity.intent != null && parentActivity.intent.extras != null) {
//            val filePath: String = parentActivity.intent.extras!!.getString("filePath") ?: ""
//            isPickedWebView = parentActivity.intent.extras!!.getBoolean("isWebView") ?: false
//
//
//            try {
//                reader = Reader()
//                reader.apply {
//                    setMaxContentPerSection(1250)
//                    setCssStatus(if (isPickedWebView) CssStatus.INCLUDE else CssStatus.OMIT)
//                    setIsIncludingTextContent(true)
//                    setIsOmittingTitleTag(true)
//                }
//
//                // This method must be called before readSection.
//                reader.setFullContent(filePath)
//
//                //int lastSavedPage = reader.setFullContentWithProgress(filePath);
//                if (reader.isSavedProgressFound) {
//                    val lastSavedPage = reader.loadProgress()
//                    viewPager.currentItem = lastSavedPage
//                }
//
//
//            } catch (e: ReadingException) {
//
//                Toast.makeText(parentActivity, e.message, Toast.LENGTH_LONG).show();
//            }
//        }
//        return view0
//    }
//
//    override fun onStop() {
//        super.onStop()
//        try {
//            reader.saveProgress(viewPager.currentItem)
//            Toast.makeText(
//                parentActivity,
//                "Saved page: " + viewPager.currentItem.toString() + "...",
//                Toast.LENGTH_LONG
//            ).show()
//        } catch (e: ReadingException) {
//            e.printStackTrace()
//            Toast.makeText(
//                parentActivity,
//                "Progress is not saved: " + e.message,
//                Toast.LENGTH_LONG
//            ).show()
//        } catch (e: OutOfPagesException) {
//            e.printStackTrace()
//            Toast.makeText(
//                parentActivity,
//                "Progress is not saved. Out of Bounds. Page Count: " + e.pageCount,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//
//    private fun overrideOnBackPressedWithCallback() {
//        val onBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                Log.d("MyLog", "handleOnBackpressed")
//                parentActivity.apply {
//                    navController.navigateChangeTitleToolbar(
//                        parentActivity,
//                        R.id.action_viewPagerFragment_to_epubReaderFragment
//                    )
//                }
//            }
//        }
//        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
//    }
//
//
//    override fun onFragmentReady(position: Int): View? {
//        var bookSection: BookSection? = null
//
//        try {
//            bookSection = reader.readSection(position)
//        } catch (e: ReadingException) {
//            e.printStackTrace()
//            Toast.makeText(parentActivity, e.message, Toast.LENGTH_LONG).show()
//        } catch (e: OutOfPagesException) {
//            e.printStackTrace()
//            pageCount = e.pageCount
//            if (isSkippedToPage) {
//                Toast.makeText(parentActivity, "Max page number is: $pageCount", Toast.LENGTH_LONG)
//                    .show()
//            }
//            mSectionPagerAdapter.notifyDataSetChanged()
//        }
//
//        isSkippedToPage = false
//
//        return if (bookSection != null) {
//            setFragmentView(isPickedWebView, bookSection.sectionContent, "text/html", "UTF-8")
//        } else null
//
//    }
//
//
//    private fun setFragmentView(
//        isContentStyled: Boolean,
//        data: String,
//        mimeType: String,
//        encoding: String
//    ): View? {
//        val layoutParams = FrameLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        return if (isContentStyled) {
//            val webView = WebView(parentActivity)
//            webView.loadDataWithBaseURL(null, data, mimeType, encoding, null)
//            webView.layoutParams = layoutParams
//            webView
//        } else {
//            val scrollView = ScrollView(parentActivity)
//            scrollView.layoutParams = layoutParams
//            val textView = TextView(parentActivity)
//            textView.layoutParams = layoutParams
//            textView.text = Html.fromHtml(data, { source ->
//                val imageAsStr = source.substring(source.indexOf(";base64,") + 8)
//                val imageAsBytes: ByteArray = Base64.decode(imageAsStr, Base64.DEFAULT)
//                val imageAsBitmap =
//                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
//                val imageWidthStartPx = (pxScreenWidth - imageAsBitmap.width) / 2
//                val imageWidthEndPx = pxScreenWidth - imageWidthStartPx
//                val imageAsDrawable: Drawable = BitmapDrawable(resources, imageAsBitmap)
//                imageAsDrawable.setBounds(
//                    imageWidthStartPx,
//                    0,
//                    imageWidthEndPx,
//                    imageAsBitmap.height
//                )
//                imageAsDrawable
//            }, null)
//            val pxPadding = dpToPx(12)
//            textView.setPadding(pxPadding, pxPadding, pxPadding, pxPadding)
//            scrollView.addView(textView)
//            scrollView
//        }
//    }
//
//
//    private fun dpToPx(dp: Int): Int {
//        val displayMetrics = resources.displayMetrics
//        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
//    }
//
//
//    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
//
//        override fun getCount(): Int {
//            return pageCount
//        }
//
//        override fun getItem(position: Int): Fragment {
//            // getItem is called to instantiate the fragment for the given page.
//            return PageFragment.newInstance(position)
//        }
//    }
}


