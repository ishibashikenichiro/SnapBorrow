class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :login_id, :limit => 16, :null => false
      t.string :nickname, :limit => 40
      t.string :mail, :limit => 40
      t.string :password_digest, :null => false
      t.datetime :delete_date

      t.timestamps
    end
    add_index :users, :login_id, :unique => true
    add_index :users, :mail, :unique => true
  end
end
