### Database

This provides storage for ordered data associated with the Annomate app platform.
The database uses three tables: users, images and legend. The relation schema for these is
provided in the user documentation. This is designed for MySQL 5.7.22, hosted on an AWS RDS instance.

To set up the database, provision a new database instance and run either the annomate-dummy-data.sql
script or annomate-no-data.sql script depending on whether this is a production or test instance.

