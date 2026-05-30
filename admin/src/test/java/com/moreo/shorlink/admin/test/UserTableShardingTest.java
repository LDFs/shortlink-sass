package com.moreo.shorlink.admin.test;

public class UserTableShardingTest {
    public static final String SQL = "CREATE TABLE `t_group_%d` (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT,\n" +
            "  `gid` varchar(32) DEFAULT NULL,\n" +
            "  `name` varchar(64) DEFAULT NULL,\n" +
            "  `username` varchar(256) DEFAULT NULL,\n" +
            "  `sort_order` int DEFAULT '0',\n" +
            "  `create_time` datetime DEFAULT NULL,\n" +
            "  `update_time` datetime DEFAULT NULL,\n" +
            "  `del_flag` tinyint(1) DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_unique_gid_username` (`gid`,`username`) USING BTREE /*!80000 INVISIBLE */\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
