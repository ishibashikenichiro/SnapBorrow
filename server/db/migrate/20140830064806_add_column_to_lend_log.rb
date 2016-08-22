class AddColumnToLendLog < ActiveRecord::Migration
  def change
    add_column :lend_logs, :temporary, :boolean, :default => false
  end
end
