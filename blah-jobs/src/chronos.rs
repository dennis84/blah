use std::collections::HashMap;

use futures::{future, Future, Stream};

use hyper::{self, Client, Url, Post, Put, Delete, StatusCode};
use hyper::client::{HttpConnector, Request};

use serde_json::from_slice;

type Fut<T> = Box<Future<Item = T, Error = hyper::Error>>;

#[derive(Serialize, Debug)]
pub struct Job {
    pub name: String,
    pub status: String,
    pub repeat: bool,
    #[serde(rename = "lastSuccess")]
    pub last_success: String,
}

#[derive(Deserialize, Debug)]
pub struct ChronosJob {
    name: String,
    schedule: String,
    #[serde(rename = "lastSuccess")]
    last_success: String,
}

#[derive(Debug)]
pub struct Message {
    pub message: String,
    pub status: StatusCode,
}

pub struct JobRepo {
    pub client: Client<HttpConnector>,
    pub chronos_url: String,
}

impl Clone for JobRepo {
    fn clone(&self) -> JobRepo {
        JobRepo {
            client: self.client.clone(),
            chronos_url: self.chronos_url.clone(),
        }
    }
}

impl JobRepo {
    pub fn list(&self) -> Fut<Vec<Job>> {
        let jobs_url = Url::parse(&format!(
                "{}/v1/scheduler/jobs",
                self.chronos_url)).unwrap();
        let csv_url = Url::parse(&format!(
                "{}/v1/scheduler/graph/csv",
                self.chronos_url)).unwrap();

        let client_inner = self.client.clone();
        let jobs = self.client.get(jobs_url).and_then(|resp| {
            resp.body().fold(Vec::new(), |mut acc, chunk| {
                acc.extend_from_slice(chunk.as_ref());
                Ok::<_, hyper::Error>(acc)
            })
        }).and_then(move |bytes| {
            let chronos_jobs = from_slice::<Vec<ChronosJob>>(&bytes).unwrap();
            client_inner.get(csv_url).and_then(|resp| {
                resp.body().fold(Vec::new(), |mut acc, chunk| {
                    acc.extend_from_slice(chunk.as_ref());
                    Ok::<_, hyper::Error>(acc)
                })
            }).map(move |bytes| {
                let data = String::from_utf8(bytes).unwrap();
                let lines = data.lines();
                let mut status_map = HashMap::new();

                for line in lines {
                    let parts = line.split(",").collect::<Vec<&str>>();
                    let name = parts.get(1);
                    let status = parts.get(3);
                    if name.is_none() || status.is_none() {
                        continue;
                    }

                    let name = name.unwrap().to_string();
                    let status = status.unwrap().to_string();
                    status_map.insert(name, status);
                }

                chronos_jobs.iter().map(|chronos_job| {
                    let status = status_map.get(&chronos_job.name);
                    let status = status.map(|x| x.clone());

                    Job {
                        name: chronos_job.name.clone(),
                        status: status.unwrap_or("idle".to_string()),
                        repeat: !chronos_job.schedule.starts_with("R0/"),
                        last_success: chronos_job.last_success.clone(),
                    }
                }).collect()
            })
        }).or_else(|e| {
            error!("Error during chronos request: {}", e);
            future::ok(Vec::new())
        });

        Box::new(jobs)
    }

    pub fn run(&self, name: &str) -> Fut<Message> {
        let url = Url::parse(&format!(
                "{}/v1/scheduler/job/{}",
                self.chronos_url,
                name)).unwrap();
        let mut req = Request::new(Put, url);
        Box::new(self.client.request(req).map(move |resp| {
            let message = match resp.status() {
                &StatusCode::NoContent => "Job has started successfully.",
                _                      => "Job could not be started.",
            };

            Message {
                message: message.to_string(),
                status: resp.status().clone(),
            } 
        }))
    }

    pub fn stop(&self, name: &str) -> Fut<Message> {
        let url = Url::parse(&format!(
                "{}/v1/scheduler/task/kill/{}",
                self.chronos_url,
                name)).unwrap();
        let req = Request::new(Delete, url);
        Box::new(self.client.request(req).map(move |resp| {
            let message = match resp.status() {
                &StatusCode::NoContent => "Job was successfully stopped.",
                _                      => "Job could not be stopped.",
            };

            Message {
                message: message.to_string(),
                status: resp.status().clone(),
            } 
        }))
    }
}
