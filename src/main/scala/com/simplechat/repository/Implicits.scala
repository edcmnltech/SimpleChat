package com.simplechat.repository

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.MySQLProfile.api._

object Implicits {
    implicit val idMapper: JdbcType[ChatRoomId] with BaseTypedType[ChatRoomId] = MappedColumnType.base[ChatRoomId, Int](_.value, new ChatRoomId(_))
    implicit val chatNameMapper: JdbcType[ChatRoomName] with BaseTypedType[ChatRoomName] = MappedColumnType.base[ChatRoomName, String](_.value, new ChatRoomName(_))
    implicit val chatUserMapper: JdbcType[ChatUsername] with BaseTypedType[ChatUsername] = MappedColumnType.base[ChatUsername, String](_.value, new ChatUsername(_))
    implicit val chatPasswordMapper: JdbcType[ChatRoomPassword] with BaseTypedType[ChatRoomPassword] = MappedColumnType.base[ChatRoomPassword, String](_.value, new ChatRoomPassword(_))
}
