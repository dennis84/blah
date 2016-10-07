use std::collections::HashMap;
use service::{Service, Statistic, App};
use error::{AutoscaleResult};
use mio::channel::{Sender};
use rustc_serialize::json::{encode};
use mio::*;

#[derive(Debug)]
pub enum Message {
    Update,
    Tick,
}

#[derive(Debug, RustcEncodable)]
pub struct AppInfo {
    pub app: String,
    pub instances: i64,
    pub max_instances: i32,
    pub cpu_usage: f64,
    pub mem_usage: f64,
}

pub struct Autoscale {
    service: Service,
    apps: HashMap<String, App>,
    stats: HashMap<String, Statistic>,
    need_update: bool,
}

impl Autoscale {
    pub fn new(service: Service) -> Autoscale {
        Autoscale {
            service: service,
            apps: HashMap::new(),
            stats: HashMap::new(),
            need_update: true,
        }
    }

    pub fn update(&mut self) -> AutoscaleResult<()> {
        self.need_update = true;
        Ok(())
    }

    pub fn tick(&mut self) -> AutoscaleResult<Vec<AppInfo>> {
        let slaves = try!(self.service.get_slaves());
        let mut apps = vec![];

        if self.need_update {
            self.apps.clear();
            self.stats.clear();
            let apps = try!(self.service.get_apps());
            for id in apps {
                let app = try!(self.service.get_app(id.as_ref()));
                if app.is_some() {
                    self.apps.insert(id, app.unwrap());
                }
            }

            self.need_update = false;
        }

        for (id, app) in &self.apps {
            let stat = {
                let prev = self.stats.get(id);
                self.service.get_statistic(&app, &slaves, prev)
            };

            if stat.is_err() {
                continue;
            }

            let stat = stat.unwrap();
            if stat.cpu_usage > app.max_cpu_usage ||
               stat.mem_usage > app.max_mem_usage {
                match self.service.scale(&app) {
                    Ok(_) => {},
                    Err(_) => {},
                }
            }

            apps.push(AppInfo {
                app: app.name.to_owned(),
                instances: app.instances,
                max_instances: app.max_instances,
                cpu_usage: stat.cpu_usage,
                mem_usage: stat.mem_usage,
            });

            self.stats.insert(id.to_owned(), stat);
        }

        Ok(apps)
    }
}
