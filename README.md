update-notifier
===============

Site update notifier webapp

Developer guide for frontend:
1. You must have Ruby (1.9.3) and Rails (3.2.13) installed. Work on other versions is not guaranteed
2. Don't forget update gems.
3. For project launch: 
	-in the root frontend folder project type 'rails s' or 'rails server'
	-project will be located at 'localhost:3000'
4. Backend connection implemented in the app/helpers/database_helper.rb. I.e., you can change connection address and port.
5. After successfull sign application must show resources page. 'Signin' and 'resources' requests to backend will done for this.