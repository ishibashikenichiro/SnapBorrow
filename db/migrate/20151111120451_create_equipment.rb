class CreateEquipment < ActiveRecord::Migration
  def change
    create_table :equipment do |t|
      t.references :equipment_class, :null=>false
	  t.string :unique_id
	  t.string :equipment_location
	  t.datetime :equipment_record_date
	  t.integer :equipment_recorder_id
                    t.integer :state
	  t.string :descr
      t.timestamps
    end
	add_index :equipment, :equipment_class_id
  end
end
