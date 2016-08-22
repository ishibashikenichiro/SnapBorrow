class CreateEquipmentLogs < ActiveRecord::Migration
  def change
    create_table :equipment_logs do |t|
      t.references :equipment, :null=>false
	  t.references :user, :null=>false
	  t.datetime :equipment_lend_date
	  t.datetime :equipment_return_date
      t.timestamps
    end
	add_index :equipment_logs, :equipment_id
	add_index :equipment_logs, :user_id
  end
end
