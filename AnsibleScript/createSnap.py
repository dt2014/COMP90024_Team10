import boto
from boto.ec2.regioninfo import RegionInfo

#connect the cloud with EC2
region=RegionInfo(name='melbourne', endpoint='nova.rc.nectar.org.au')
ec2_conn = boto.connect_ec2(aws_access_key_id='2e9e79d520114de69c3653d04cf2ac3b', 
		aws_secret_access_key='0fedbb1686264bac99dd4ac83218620a', is_secure=True, region=region, port=8773, 
			path='/services/Cloud', validate_certs=False)

#get all the volume IDs
volIDs = []
curr_vol = ec2_conn.get_all_volumes()
for volid in curr_vol:
	volIDs.append(volid.id)

# create snapshot for corresponding volume
for v in volIDs:
	snapName = "snapshot"+"-"+v
	ec2_conn.create_snapshot(v, snapName)