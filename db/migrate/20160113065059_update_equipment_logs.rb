class UpdateEquipmentLogs < ActiveRecord::Migration
  def change
   add_column :equipment_logs, :group_id, :integer
  end

  def down
  end
end
