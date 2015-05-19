'''
    * Team 10
    * New York
    * Fengmin Deng      659332
    * Jiajie Li         631482
    * Shuangchao Yin    612511
    * Weijia Chen       616213
    * Yun Shi           621761
'''
import boto
import time
from boto.ec2.regioninfo import RegionInfo

#connect the cloud with EC2
region=RegionInfo(name='melbourne', endpoint='nova.rc.nectar.org.au')
ec2_conn = boto.connect_ec2(aws_access_key_id='2e9e79d520114de69c3653d04cf2ac3b', 
		aws_secret_access_key='0fedbb1686264bac99dd4ac83218620a', is_secure=True, region=region, port=8773, 
			path='/services/Cloud', validate_certs=False)

# initialize instance and volume
instNum = 3
for k in range(instNum):
	#create instances
	ec2_conn.run_instances('ami-000032bd', key_name='cloudKey', instance_type='m1.small', security_groups=['http','ssh'])
	#create volumes
	vol_req = ec2_conn.create_volume(30,"tasmania")

reservations = ec2_conn.get_all_reservations()

#get instance objects, IDs, IPs and status
instances = [i for r in reservations for i in r.instances]
instIDs = [i.id for i in instances]
instIPs = [i.ip_address for i in instances]
instSta = [i.state for i in instances]

#get all the volume IDs
volIDs = []
curr_vol = ec2_conn.get_all_volumes()
for volid in curr_vol:
	volIDs.append(volid.id)

# function of checking status of instance and return a boolean	
def checkSame(a):
	count = 0
	judge = None
	for i in range(len(a)):
		if (a[0] == a[i]) and (a[0] == 'running'):
			count += 1
	if count == len(a):
		judge = True
	else:
		judge = False
	return judge

# check the instance status and sleep 1 min if not all running 
while True:
	reservations = ec2_conn.get_all_reservations()
	instances = [i for r in reservations for i in r.instances]
	instSta = [i.state for i in instances]
	if checkSame(instSta):
		break
	else:
		time.sleep(30)

# if all instance running, attack volumes to corresponding instances
for k in range(instNum):
	ec2_conn.attach_volume(volIDs[k],instIDs[k],"/dev/vdc")

# write the host ip_address as a file for ansible to implement
hostPath = "/Users/jacky/Desktop/cloud/textHost"
with open(hostPath, 'w') as f:
	content = "[cloudHost]\n"+"\n".join(instIPs)+"\n\n[cloudHost:vars]\nansible_ssh_user=ubuntu\nansible_ssh_private_key_file=/home/chenvega/Desktop/cloud/cloudKey.key"
	f.write(content)
