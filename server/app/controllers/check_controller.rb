class CheckController<ApplicationController
 def  equipmentcheck
	equipmentname=params[:_json].first[:equipmentname]
        userid=params[:_json].first[:userid]
        groupid=params[:_json].first[:groupid]
	puts "File received NAME="+equipmentname
             if EquipmentClass.find_by_group_id_and_equipment_classes_name(groupid,equipmentname)
                render :text=>"1"
             else
                render :text=>"0"
             end
                     
 end
end