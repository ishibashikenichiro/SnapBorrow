class CreateLendLogs < ActiveRecord::Migration
  def change
    create_table :lend_logs do |t|
      t.references :book, :null => false
      t.references :user, :null => false
      t.foreign_key :books
      t.foreign_key :users

      t.datetime :lend_date, :null => false
      t.datetime :return_date
      t.datetime :cancel_date

      t.timestamps
    end
    add_index :lend_logs, [:book_id, :user_id]
  end
end
