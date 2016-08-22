class AddStateToEquipment < ActiveRecord::Migration
  def change
    add_column :equipment, :state, :integer
  end
end
