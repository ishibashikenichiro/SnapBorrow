class CreateDeviceIds < ActiveRecord::Migration
  def change
    create_table :device_ids do |t|
      t.references :user
      t.string :registration_id, :null => false

      t.foreign_key :users

      t.timestamps
    end
  end
end
