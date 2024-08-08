#!/bin/bash

# create directory for backups
mkdir -p /backups

# Set up the cron job
(crontab -l 2>/dev/null; echo "0 12 * * * mysqldump -uleon -p'vaqneb-Rucgar-turfe6' -A > /backups/all_databases_backup_$(date +\%Y-\%m-\%d-\%H\%M\%S).sql") | crontab -

# Start the cron service
service cron start

# Execute the original MySQL entrypoint script
exec /usr/local/bin/docker-entrypoint.sh "$@"
