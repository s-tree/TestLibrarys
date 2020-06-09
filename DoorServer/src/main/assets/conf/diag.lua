--[[
std::string interface_name = root["interface"].asString();
std::string ip = root["ip"].asString();
std::string gateway = root["gateway"].asString();
bool connect = root["connect"].asBool();
设置接口c_setresult
]]--

function do_popen(cmd,err)
	local t = assert(io.popen(cmd))
	if(t == nil) then
		return nil
	else
		l = t:read("*all")
		t:close()
		if(err ~= nil) then
			k = string.find(l,err)
			if(k == nil) then
				return l
			else
				return nil
			end
		end
		return l
	end
end

function main()
	local interface_name = "eth0.3"
	local str = do_popen("ifconfig eth0.3")
	local ip = ""
	local gateway = ""
	local connect = false
	
	_,_,ip = string.find(str,"inet addr:(%S+)")
	
	if(ip == nil) then
		ip = ""
		gateway = ""
		connect = false
	else
		str = do_popen("netstat -r |grep default|awk \'{print $2}\'")
		_,_,gateway = string.find(str,"(%C+)")
		if(gateway == nil) then
			gateway = ""
			connect = false
		else
			local temp = string.format("ping -c 1 -w 1 %s |grep ttl | awk \'{print $7}\' | awk -F\"=\" \'{print $2}\'",gateway)
			local result = do_popen(temp)
			if(result ~= nil) then
				result_check = string.find(result,".")
				if(result_check ~= nil) then
					connect = true
					print("ping result:"..result.."end")
				end			
			end
		end
	end
	
	if(connect == true) then
		result = string.format("{\"interface\":\"%s\",\"ip\":\"%s\",\"gateway\":\"%s\",\"connect\":true}",interface_name,ip,gateway)	
	else
		result = string.format("{\"interface\":\"%s\",\"ip\":\"%s\",\"gateway\":\"%s\",\"connect\":false}",interface_name,ip,gateway)
	end

	print(result)
	
	c_setresult(result)
end

main()
