class CreateEquipmentClasses < ActiveRecord::Migration
  def change
    create_table :equipment_classes do |t|
	 
      t.references :group, :null=>false
	  t.string :equipment_classes_name, :limit=>13
	  t.integer :recognizer_number
      t.timestamps
    end
	add_index :equipment_classes, :group_id
  end
end
