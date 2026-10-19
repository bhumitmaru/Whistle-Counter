package com.whistlecounter.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whistlecounter.app.ui.theme.BorderSoft
import com.whistlecounter.app.ui.theme.MutedSoft
import com.whistlecounter.app.ui.theme.Terracotta
import com.whistlecounter.app.ui.theme.TerracottaPale
import com.whistlecounter.app.ui.theme.WarmWhite

private data class TabItem(
    val label: String,
    val icon: ImageVector,
)

private val tabs = listOf(
    TabItem("Counter", Icons.Rounded.LocalFireDepartment),
    TabItem("Guide", Icons.Outlined.MenuBook),
    TabItem("FAQ", Icons.Outlined.HelpOutline),
    TabItem("About", Icons.Outlined.Info),
)

@Composable
fun AppBottomBar(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    isListening: Boolean,
) {
    Surface(color = WarmWhite, shadowElevation = 14.dp) {
        Column {
            HorizontalDivider(color = BorderSoft)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(62.dp)
                    .selectableGroup(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEachIndexed { index, item ->
                    val selected = selectedTab == index
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(62.dp)
                            .selectable(
                                selected = selected,
                                role = Role.Tab,
                                onClick = { onSelectTab(index) },
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) TerracottaPale else WarmWhite)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = if (selected) Terracotta else MutedSoft,
                                modifier = Modifier.size(20.dp),
                            )
                            if (index == 0 && isListening) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 3.dp)
                                        .size(6.dp)
                                        .background(Terracotta, RoundedCornerShape(3.dp)),
                                )
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            ),
                            color = if (selected) Terracotta else MutedSoft,
                        )
                    }
                }
            }
        }
    }
}
