class CreateErrorReports < ActiveRecord::Migration
  def change
    create_table :error_reports do |t|
      t.integer :user_id
      t.string :detail, :limit => 2000

      t.timestamps
    end
  end
end
