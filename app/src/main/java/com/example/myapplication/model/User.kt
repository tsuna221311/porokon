package com.example.myapplication.model

import kotlinx.serialization.Serializable

/**
 * ユーザー情報を保持するデータクラス。
 * @Serializableアノテーションにより、JSONとの間で自動的に変換が可能になります。
 */
@Serializable
data class User(
    // TODO: サーバーのAPI仕様に合わせて、実際のプロパティ（id, name, emailなど）に修正してください。
    val id: String,
    val name: String,
    val email: String
)
