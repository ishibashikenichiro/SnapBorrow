class CreateEquipmentTestTables < ActiveRecord::Migration
  def change
    create_table :equipment_test_tables do |t|
    	t.string :name
    	t.integer :state
      t.timestamps
    end
  end
end
