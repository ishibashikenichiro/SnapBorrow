class CreateNotificationLogs < ActiveRecord::Migration
  def change
    create_table :notification_logs do |t|
      t.references :user, :null => false
      t.string :message,  :null => false
      t.datetime :date,  :null => false
      t.boolean :is_notify,  :null => false

      t.foreign_key :users

      t.timestamps
    end
  end
end
