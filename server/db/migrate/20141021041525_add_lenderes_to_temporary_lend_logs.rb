class AddLenderesToTemporaryLendLogs < ActiveRecord::Migration
  def change
    add_column :temporary_lend_logs, :lenders, :integer
  end
end
