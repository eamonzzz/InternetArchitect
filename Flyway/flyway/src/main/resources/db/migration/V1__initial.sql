/*
 Navicat Premium Data Transfer

 Source Server         : postgresql-local
 Source Server Type    : PostgreSQL
 Source Server Version : 120002
 Source Host           : localhost:5432
 Source Catalog        : flyway-dev
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120002
 File Encoding         : 65001

 Date: 28/11/2021 21:45:15
*/


-- ----------------------------
-- Sequence structure for order_entity_id_seq
-- ----------------------------
DROP
SEQUENCE IF EXISTS "public"."order_entity_id_seq";
CREATE
SEQUENCE "public"."order_entity_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START
1
CACHE
1;
ALTER
SEQUENCE "public"."order_entity_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for user_entity_id_seq
-- ----------------------------
DROP
SEQUENCE IF EXISTS "public"."user_entity_id_seq";
CREATE
SEQUENCE "public"."user_entity_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START
1
CACHE
1;
ALTER
SEQUENCE "public"."user_entity_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for order_entity
-- ----------------------------
DROP TABLE IF EXISTS "public"."order_entity";
CREATE TABLE "public"."order_entity"
(
    "id" int8 NOT NULL DEFAULT nextval('order_entity_id_seq'::regclass),
    "description" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "type"        varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."order_entity" OWNER TO "postgres";

-- ----------------------------
-- Table structure for user_entity
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_entity";
CREATE TABLE "public"."user_entity"
(
    "id" int8 NOT NULL DEFAULT nextval('user_entity_id_seq'::regclass),
    "password" varchar(255) COLLATE "pg_catalog"."default",
    "username" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."user_entity" OWNER TO "postgres";

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER
SEQUENCE "public"."order_entity_id_seq"
OWNED BY "public"."order_entity"."id";
SELECT setval('"public"."order_entity_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER
SEQUENCE "public"."user_entity_id_seq"
OWNED BY "public"."user_entity"."id";
SELECT setval('"public"."user_entity_id_seq"', 2, true);

-- ----------------------------
-- Indexes structure for table flyway_schema_history
-- ----------------------------
CREATE INDEX "flyway_schema_history_s_idx" ON "public"."flyway_schema_history" USING btree (
    "success" "pg_catalog"."bool_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table order_entity
-- ----------------------------
ALTER TABLE "public"."order_entity"
    ADD CONSTRAINT "order_entity_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table user_entity
-- ----------------------------
ALTER TABLE "public"."user_entity"
    ADD CONSTRAINT "user_entity_pkey" PRIMARY KEY ("id");
