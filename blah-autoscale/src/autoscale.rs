use std::collections::HashMap;
use service::{Service, Statistic, App};
use error::{AutoscaleResult};
use mio::channel::{Sender};
use rustc_serialize::json::{encode};

#[derive(Debug, RustcEncodable)]
pub struct AppInfo {
    app: String,
    instances: i64,
    max_instances: i32,
    cpu_usage: f64,
    mem_usage: f64,
}

pub trait Output {
    fn write(&self, info: AppInfo);
}

pub struct ConsoleOutput {}
pub struct SSEOutput {
    pub sender: Sender<String>,
}

impl Output for ConsoleOutput {
    fn write(&self, info: AppInfo) {
        log_app_info(&info);
    }
}

impl Output for SSEOutput {
    fn write(&self, info: AppInfo) {
        log_app_info(&info);
        self.sender.send(encode(&info).unwrap()).unwrap();
    }
}

fn log_app_info(info: &AppInfo) {
    info!("----------------------------------------");
    info!("App: {}", info.app);
    info!("Instances: {}/{}", info.instances, info.max_instances);
    info!("CPU: {}", info.cpu_usage);
    info!("MEM: {}", info.mem_usage);
}

pub struct Autoscale<O> {
    service: Service,
    apps: HashMap<String, App>,
    stats: HashMap<String, Statistic>,
    need_update: bool,
    output: O,
}

impl<O: Output> Autoscale<O> {
    pub fn new(service: Service, output: O) -> Autoscale<O> {
        Autoscale {
            service: service,
            apps: HashMap::new(),
            stats: HashMap::new(),
            need_update: true,
            output: output,
        }
    }

    pub fn update(&mut self) -> AutoscaleResult<()> {
        self.need_update = true;
        Ok(())
    }

    pub fn tick(&mut self) -> AutoscaleResult<()> {
        let slaves = try!(self.service.get_slaves());

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

            self.output.write(AppInfo {
                app: app.name.to_owned(),
                instances: app.instances,
                max_instances: app.max_instances,
                cpu_usage: stat.cpu_usage,
                mem_usage: stat.mem_usage,
            });

            self.stats.insert(id.to_owned(), stat);
        }

        Ok(())
    }
}
