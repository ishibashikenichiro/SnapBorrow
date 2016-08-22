class CreateEquipmentregistrations < ActiveRecord::Migration
  def change
    create_table :equipmentregistrations do |t|

      t.timestamps
    end
  end
end
