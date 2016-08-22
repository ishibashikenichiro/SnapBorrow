# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20160113065059) do

  create_table "action_logs", :force => true do |t|
    t.integer  "user_id",    :null => false
    t.integer  "group_id"
    t.string   "device",     :null => false
    t.string   "action",     :null => false
    t.datetime "date",       :null => false
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "action_logs", ["user_id"], :name => "action_logs_user_id_fk"

  create_table "book_catalogs", :force => true do |t|
    t.string   "book_code",        :limit => 13
    t.string   "title",            :limit => 100, :null => false
    t.string   "authors",          :limit => 100
    t.string   "publisher",        :limit => 40
    t.datetime "publication_date"
    t.datetime "created_at",                      :null => false
    t.datetime "updated_at",                      :null => false
    t.string   "image"
    t.integer  "group_id",                        :null => false
  end

  add_index "book_catalogs", ["book_code"], :name => "index_book_catalogs_on_book_code"

  create_table "books", :force => true do |t|
    t.string   "book_code",   :limit => 13,                    :null => false
    t.integer  "group_id",                                     :null => false
    t.integer  "shelf_id",                                     :null => false
    t.datetime "delete_date"
    t.boolean  "state",                     :default => false, :null => false
    t.datetime "created_at",                                   :null => false
    t.datetime "updated_at",                                   :null => false
  end

  create_table "device_ids", :force => true do |t|
    t.integer  "user_id"
    t.string   "registration_id", :null => false
    t.datetime "created_at",      :null => false
    t.datetime "updated_at",      :null => false
  end

  add_index "device_ids", ["user_id"], :name => "device_ids_user_id_fk"

  create_table "equipment", :force => true do |t|
    t.integer  "equipment_class_id",    :null => false
    t.string   "unique_id"
    t.string   "equipment_location"
    t.datetime "equipment_record_date"
    t.integer  "equipment_recorder_id"
    t.string   "descr"
    t.datetime "created_at",            :null => false
    t.datetime "updated_at",            :null => false
    t.integer  "state"
  end

  add_index "equipment", ["equipment_class_id"], :name => "index_equipment_on_equipment_class_id"

  create_table "equipment_classes", :force => true do |t|
    t.integer  "group_id",                             :null => false
    t.string   "equipment_classes_name", :limit => 13
    t.integer  "recognizer_number"
    t.datetime "created_at",                           :null => false
    t.datetime "updated_at",                           :null => false
  end

  add_index "equipment_classes", ["group_id"], :name => "index_equipment_classes_on_group_id"

  create_table "equipment_logs", :force => true do |t|
    t.integer  "equipment_id",          :null => false
    t.integer  "user_id",               :null => false
    t.datetime "equipment_lend_date"
    t.datetime "equipment_return_date"
    t.datetime "created_at",            :null => false
    t.datetime "updated_at",            :null => false
    t.integer  "group_id"
  end

  add_index "equipment_logs", ["equipment_id"], :name => "index_equipment_logs_on_equipment_id"
  add_index "equipment_logs", ["user_id"], :name => "index_equipment_logs_on_user_id"

  create_table "equipment_test_tables", :force => true do |t|
    t.string   "name"
    t.integer  "state"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "equipmentregistrations", :force => true do |t|
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "error_reports", :force => true do |t|
    t.integer  "user_id"
    t.string   "detail",     :limit => 2000
    t.datetime "created_at",                 :null => false
    t.datetime "updated_at",                 :null => false
  end

  create_table "fixtures_tests", :force => true do |t|
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "groups", :force => true do |t|
    t.string   "name",        :limit => 40, :null => false
    t.datetime "delete_date"
    t.datetime "created_at",                :null => false
    t.datetime "updated_at",                :null => false
  end

  create_table "invitation_logs", :force => true do |t|
    t.integer  "group_id",   :null => false
    t.string   "mail"
    t.datetime "expire_at"
    t.string   "url_hash"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "invitation_logs", ["group_id"], :name => "invitation_logs_group_id_fk"

  create_table "lend_logs", :force => true do |t|
    t.integer  "book_id",                        :null => false
    t.integer  "user_id",                        :null => false
    t.datetime "lend_date",                      :null => false
    t.datetime "return_date"
    t.datetime "cancel_date"
    t.datetime "created_at",                     :null => false
    t.datetime "updated_at",                     :null => false
    t.boolean  "temporary",   :default => false
    t.string   "book_code",                      :null => false
  end

  add_index "lend_logs", ["book_id", "user_id"], :name => "index_lend_logs_on_book_id_and_user_id"
  add_index "lend_logs", ["user_id"], :name => "lend_logs_user_id_fk"

  create_table "managers", :force => true do |t|
    t.integer  "group_id",   :null => false
    t.integer  "user_id",    :null => false
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "managers", ["group_id", "user_id"], :name => "index_managers_on_group_id_and_user_id", :unique => true
  add_index "managers", ["user_id"], :name => "managers_user_id_fk"

  create_table "members", :force => true do |t|
    t.integer  "group_id",   :null => false
    t.integer  "user_id",    :null => false
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "members", ["group_id", "user_id"], :name => "index_members_on_group_id_and_user_id"
  add_index "members", ["user_id"], :name => "members_user_id_fk"

  create_table "notification_logs", :force => true do |t|
    t.integer  "user_id",    :null => false
    t.string   "message",    :null => false
    t.datetime "date",       :null => false
    t.boolean  "is_notify",  :null => false
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  add_index "notification_logs", ["user_id"], :name => "notification_logs_user_id_fk"

  create_table "password_resets", :force => true do |t|
    t.integer  "user_id",    :null => false
    t.string   "url_hash",   :null => false
    t.datetime "expire_at",  :null => false
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "recognizepicnames", :force => true do |t|
    t.integer  "group_id"
    t.integer  "user_id"
    t.string   "picname"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "registerpicnames", :force => true do |t|
    t.integer  "group_id"
    t.integer  "user_id"
    t.integer  "equipment_classes_id"
    t.string   "picname"
    t.datetime "created_at",           :null => false
    t.datetime "updated_at",           :null => false
  end

  create_table "roundtable_files", :force => true do |t|
    t.integer  "user_id",                      :null => false
    t.integer  "roundtable_id",                :null => false
    t.string   "title",         :limit => 256
    t.string   "keywords",      :limit => 256
    t.string   "filename",      :limit => 256
    t.datetime "created_at",                   :null => false
    t.datetime "updated_at",                   :null => false
    t.datetime "uploadtime",                   :null => false
  end

  add_index "roundtable_files", ["roundtable_id"], :name => "index_roundtable_files_on_roundtable_id"
  add_index "roundtable_files", ["user_id"], :name => "index_roundtable_files_on_user_id"

  create_table "roundtable_presenters", :force => true do |t|
    t.integer  "roundtable_id", :null => false
    t.integer  "user_id",       :null => false
    t.datetime "created_at",    :null => false
    t.datetime "updated_at",    :null => false
  end

  add_index "roundtable_presenters", ["roundtable_id", "user_id"], :name => "index_roundtable_presenters_on_roundtable_id_and_user_id"
  add_index "roundtable_presenters", ["user_id"], :name => "roundtable_presenters_user_id_fk"

  create_table "roundtables", :force => true do |t|
    t.integer  "group_id",                     :null => false
    t.string   "title",         :limit => 128, :null => false
    t.string   "keywords",      :limit => 256
    t.string   "book_code",     :limit => 13
    t.datetime "startdatetime"
    t.datetime "enddatetime"
    t.string   "place",         :limit => 40
    t.datetime "created_at",                   :null => false
    t.datetime "updated_at",                   :null => false
  end

  add_index "roundtables", ["group_id"], :name => "index_roundtables_on_group_id"

  create_table "shelves", :force => true do |t|
    t.integer  "shelf_id",                  :null => false
    t.integer  "group_id",                  :null => false
    t.string   "name",        :limit => 40, :null => false
    t.datetime "delete_date"
    t.datetime "created_at",                :null => false
    t.datetime "updated_at",                :null => false
  end

  add_index "shelves", ["group_id"], :name => "shelves_group_id_fk"
  add_index "shelves", ["shelf_id", "group_id"], :name => "index_shelves_on_shelf_id_and_group_id", :unique => true

  create_table "temporary_lend_logs", :force => true do |t|
    t.string   "book_code"
    t.integer  "user_id",     :null => false
    t.integer  "group_id",    :null => false
    t.datetime "lend_date",   :null => false
    t.datetime "return_date"
    t.integer  "status"
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
    t.integer  "lenders"
    t.integer  "possessors"
  end

  add_index "temporary_lend_logs", ["group_id"], :name => "temporary_lend_logs_group_id_fk"
  add_index "temporary_lend_logs", ["user_id"], :name => "temporary_lend_logs_user_id_fk"

  create_table "users", :force => true do |t|
    t.string   "login_id",        :limit => 16
    t.string   "nickname",        :limit => 40
    t.string   "mail"
    t.string   "password_digest",               :null => false
    t.datetime "delete_date"
    t.datetime "created_at",                    :null => false
    t.datetime "updated_at",                    :null => false
  end

  add_index "users", ["login_id"], :name => "index_users_on_login_id", :unique => true
  add_index "users", ["mail"], :name => "index_users_on_mail", :unique => true

  add_foreign_key "action_logs", "users", name: "action_logs_user_id_fk"

  add_foreign_key "device_ids", "users", name: "device_ids_user_id_fk"

  add_foreign_key "invitation_logs", "groups", name: "invitation_logs_group_id_fk"

  add_foreign_key "lend_logs", "books", name: "lend_logs_book_id_fk"
  add_foreign_key "lend_logs", "users", name: "lend_logs_user_id_fk"

  add_foreign_key "managers", "groups", name: "managers_group_id_fk"
  add_foreign_key "managers", "users", name: "managers_user_id_fk"

  add_foreign_key "members", "groups", name: "members_group_id_fk"
  add_foreign_key "members", "users", name: "members_user_id_fk"

  add_foreign_key "notification_logs", "users", name: "notification_logs_user_id_fk"

  add_foreign_key "roundtable_presenters", "roundtables", name: "roundtable_presenters_roundtable_id_fk"
  add_foreign_key "roundtable_presenters", "users", name: "roundtable_presenters_user_id_fk"

  add_foreign_key "shelves", "groups", name: "shelves_group_id_fk"

  add_foreign_key "temporary_lend_logs", "groups", name: "temporary_lend_logs_group_id_fk"
  add_foreign_key "temporary_lend_logs", "users", name: "temporary_lend_logs_user_id_fk"

end
