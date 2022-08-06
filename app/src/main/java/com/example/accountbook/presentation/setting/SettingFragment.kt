package com.example.accountbook.presentation.setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.accountbook.R
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.FragmentSettingBinding
import com.example.accountbook.presentation.viewmodel.SettingViewModel
import com.example.accountbook.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    companion object {
        const val TAG = "SettingFragment"
    }

    private var _binding: FragmentSettingBinding? = null
    val binding get() = checkNotNull(_binding)
    private val settingViewModel: SettingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        _binding!!.lifecycleOwner = viewLifecycleOwner
        binding.settingComposeView.setContent {
            MainContents()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchItem()
    }

    private fun fetchItem() {
        settingViewModel.fetchItem()
    }

    private fun fragmentTransaction() {
        parentFragmentManager.commit {
            addToBackStack("SettingFragment")
            var fragment =
                parentFragmentManager.findFragmentByTag(SettingDetailFragment::class.simpleName)
            fragment = if (fragment == null) {
                SettingDetailFragment()
            } else {
                fragment as SettingDetailFragment
            }
            replace(
                R.id.main_fragment_container_view,
                fragment,
                SettingDetailFragment::class.simpleName
            )
        }
    }


    @Composable
    fun ColumnItem(
        modifier: Modifier,
        fontSize: TextUnit,
        fontResId: Int,
        textColor: Color,
        textBottomPadding: Dp,
        text: String
    ) {
        Box(
            modifier = Modifier.background(PrimaryOffWhite)
        ) {
            Column(
                modifier = modifier
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = textBottomPadding),
                    text = text,
                    color = textColor,
                    fontSize = fontSize,
                    fontFamily = FontFamily(Font(fontResId))
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(PrimaryPurple40),
                )
            }
        }
    }

    @Composable
    fun CategoryBodyItem(
        categoryName: String,
        categoryColor: String,
        onClick: () -> Unit
    ) {
        Box(modifier = Modifier.background(PrimaryOffWhite)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .background(PrimaryOffWhite)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() } // This is mandatory
                    ) { onClick() }
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
                        text = categoryName,
                        color = PrimaryPurple,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.ko_pub_world_dobum_pro_bold_700))
                    )
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(18.dp)
                            .background(
                                Color(categoryColor.toColorInt()),
                                shape = RoundedCornerShape(999.dp)
                            ), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = categoryName,
                            color = PrimaryWhite,
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(R.font.ko_pub_world_dobum_pro_bold_700))
                        )
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(PrimaryPurple40),
                )
            }
        }
    }

    @Composable
    fun FooterItem(
        footerText: String,
        onClick: () -> Unit
    ) {
        Box(modifier = Modifier.background(PrimaryOffWhite)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryOffWhite)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() } // This is mandatory
                    ) { onClick() }
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 13.dp),
                        text = footerText,
                        color = PrimaryPurple,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.ko_pub_world_dobum_pro_bold_700))
                    )
                    Image(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .padding(bottom = 1.dp),
                        painter = painterResource(id = R.drawable.ic_add_purple),
                        contentDescription = "추가하기"
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(PrimaryPurple40),
                )
            }
        }
    }

    fun pxToDp(px: Int): Float {
        val resources = requireContext().resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi / 160f)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainContents() {

        val scrollState = rememberLazyListState()
        val expenseCategoryList by settingViewModel.expenseCategoryItemList.observeAsState(emptyList())
        val incomeCategoryList by settingViewModel.incomeCategoryItemList.observeAsState(emptyList())
        val paymentList by settingViewModel.paymentItemList.observeAsState(emptyList())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryOffWhite),
            contentPadding = PaddingValues(
                bottom = pxToDp(
                    settingViewModel.bottomNavigationHeight
                ).dp
            )
        ) {
            stickyHeader {
                ColumnItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp)
                        .background(PrimaryOffWhite)
                        .animateItemPlacement(),
                    16.sp,
                    R.font.ko_pub_world_dobum_pro_medium_500,
                    PrimaryLightPurple,
                    8.dp,
                    "결제수단"
                )
            }
            itemsIndexed(paymentList!!) { _: Int, item: Payments ->
                ColumnItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 12.dp, 16.dp)
                        .background(PrimaryOffWhite)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() } // This is mandatory
                        ) {
                            fragmentTransaction()
                            settingViewModel.setProperties(
                                title = resources.getString(R.string.setting_detail_app_bar_title_payment_edit),
                                updateMode = true,
                                paymentMode = true,
                                name = item.payment,
                                expenseMode = false,
                                paymentId = item.paymentId
                            )
                        }
                        .animateItemPlacement(),
                    14.sp,
                    R.font.ko_pub_world_dobum_pro_bold_700,
                    PrimaryPurple,
                    12.dp,
                    item.payment
                )
            }
            item {
                FooterItem("결제수단 추가하기") {
                    fragmentTransaction()
                    settingViewModel.setProperties(
                        title = resources.getString(R.string.setting_detail_app_bar_title_payment_add),
                        updateMode = false,
                        paymentMode = true,
                        name = "",
                        expenseMode = false
                    )
                }
            }

            stickyHeader {
                ColumnItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp)
                        .background(PrimaryOffWhite),
                    16.sp,
                    R.font.ko_pub_world_dobum_pro_medium_500,
                    PrimaryLightPurple,
                    8.dp,
                    "지출 카테고리"
                )
            }
            itemsIndexed(expenseCategoryList!!) { _: Int, item: Categories ->
                CategoryBodyItem(item.category, item.labelColor) {
                    fragmentTransaction()
                    settingViewModel.setProperties(
                        title = resources.getString(R.string.setting_detail_app_bar_title_expense_category_edit),
                        updateMode = true,
                        paymentMode = false,
                        name = item.category,
                        expenseMode = true,
                        item.labelColor,
                        categoryId = item.categoryId
                    )
                }
            }
            item {
                FooterItem("지출 카테고리 추가하기") {
                    fragmentTransaction()
                    settingViewModel.setProperties(
                        title = resources.getString(R.string.setting_detail_app_bar_title_expense_category_add),
                        updateMode = false,
                        paymentMode = false,
                        name = "",
                        expenseMode = true
                    )
                }
            }

            stickyHeader {
                ColumnItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp)
                        .background(PrimaryOffWhite),
                    16.sp,
                    R.font.ko_pub_world_dobum_pro_medium_500,
                    PrimaryLightPurple,
                    8.dp,
                    "수입 카테고리"
                )

            }
            itemsIndexed(incomeCategoryList!!) { _: Int, item: Categories ->
                CategoryBodyItem(item.category, item.labelColor) {
                    fragmentTransaction()
                    settingViewModel.setProperties(
                        title = resources.getString(R.string.setting_detail_app_bar_title_income_category_edit),
                        updateMode = true,
                        paymentMode = false,
                        name = item.category,
                        expenseMode = false,
                        item.labelColor,
                        categoryId = item.categoryId
                    )
                }
            }
            item {
                FooterItem("수입 카테고리 추가하기") {
                    fragmentTransaction()
                    settingViewModel.setProperties(
                        title = resources.getString(R.string.setting_detail_app_bar_title_income_category_add),
                        updateMode = false,
                        paymentMode = false,
                        name = "",
                        expenseMode = false
                    )
                }
            }

        }
    }

    @Preview
    @Composable
    fun PreviewHeader() {
        ColumnItem(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp)
                .background(PrimaryOffWhite),
            16.sp,
            R.font.ko_pub_world_dobum_pro_medium_500,
            PrimaryLightPurple,
            8.dp,
            "결제수단"
        )
    }

    @Preview
    @Composable
    fun PreviewSetting() {
        MainContents()
    }
}