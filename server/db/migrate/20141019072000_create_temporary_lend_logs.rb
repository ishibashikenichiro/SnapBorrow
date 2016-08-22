class CreateTemporaryLendLogs < ActiveRecord::Migration
  def change
    create_table :temporary_lend_logs do |t|
      t.string :book_code
      t.references :user, :null => false
      t.references :group, :null => false
      t.datetime :lend_date, :null => false
      t.datetime :return_date
      t.integer :status

      t.foreign_key :users
      t.foreign_key :groups

      t.timestamps
    end
  end
end
