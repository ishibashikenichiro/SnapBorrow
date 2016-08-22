class ChangeColumnToUser < ActiveRecord::Migration
  def up
    change_column :users, :login_id, :string, :limit => 16, :null => true
    change_column :users, :mail, :string, :limit => 255, :null => true
  end

  def down
    change_column :users, :login_id, :string,  :limit => 16, :null => false
    change_column :users, :mail, :string,  :limit => 40
  end
end
