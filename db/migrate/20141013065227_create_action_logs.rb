class CreateActionLogs < ActiveRecord::Migration
  def change
    create_table :action_logs do |t|
      t.references :user, :null => false
      t.integer  :group_id
      t.string :device, :null => false
      t.string :action, :null => false
      t.datetime :date, :null => false

      t.foreign_key :users

      t.timestamps
    end
  end
end
