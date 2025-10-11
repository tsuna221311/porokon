package com.example.myapplication.data

// アプリ全体で共有するダミーの編み図データ
// データ定義をこのファイル一箇所に集約する
val dummyPatternFromImage = listOf(
    listOf("-", "-", "-", "-", "-", "yo", "-", "-"),
    listOf("-", "-", "-", "yo", "k", "k", "-", "-"),
    listOf("-", "-", "yo", "k", "k", "-", "-", "-"),
    listOf("-", "yo", "k", "k", "-", "-", "-", "-"),
    listOf("yo", "k", "k", "-", "-", "-", "-", "-"),
    listOf("k", "k", "ssk", "k", "yo", "-", "-", "-"),
    listOf("k", "k", "k", "ssk", "k", "yo", "-", "-"),
    listOf("k", "k", "k", "k", "k", "k3tog", "-", "-"),
    listOf("k", "k", "k", "k", "k2tog", "k", "yo", "-"),
    listOf("k", "k", "k", "k2tog", "k", "k", "yo", "-"),
    listOf("k", "k", "k2tog", "k", "k", "k", "k2tog", "k")
).reversed()

// ★★★ ここから追記 ★★★

// 上記のグリッドデータを英文パターンに変換したダミーデータ
val dummyEnglishPatternInstructions = listOf(
    "YO",
    "YO, K2",
    "YO, K2",
    "YO, K2",
    "YO, K2",
    "K2, SSK, K, YO",
    "K3, SSK, K, YO",
    "K5, K3TOG",
    "K4, K2TOG, K, YO",
    "K3, K2TOG, K2, YO",
    "K2, K2TOG, K3, K2TOG, K"
).reversed() // reversed()でグリッドの順序と合わせる

// 英文パターンで使われる略語の凡例ダミーデータ
val dummyAbbreviations = mapOf(
    "k" to "knit",
    "yo" to "yarn over",
    "ssk" to "slip, slip, knit",
    "k2tog" to "knit 2 together",
    "k3tog" to "knit 3 together"
)
