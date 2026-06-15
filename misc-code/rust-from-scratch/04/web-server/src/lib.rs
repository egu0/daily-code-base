use std::{
    sync::{mpsc, Arc, Mutex},
    thread,
};

struct Worker {
    //Worker 编号
    id: usize,

    //工作线程
    thread: Option<thread::JoinHandle<()>>,
}

impl Worker {
    fn new(id: usize, receiver: Arc<Mutex<mpsc::Receiver<Message>>>) -> Worker {
        //参考：https://github.com/rust-lang-cn/book-cn/blob/7c80eeb6d5730438e385abad360bf5d2715317f9/src/ch20-02-multithreaded.md#%E5%AE%9E%E7%8E%B0-execute-%E6%96%B9%E6%B3%95
        let thread = thread::spawn(move || loop {
            let message = receiver.lock().unwrap().recv().unwrap();
            match message {
                Message::NewJob(job) => {
                    println!("Worker-{} got a job; excuting...", id);
                    job();
                }
                Message::Terminate => {
                    println!("Worker-{} was told to terminate.", id);
                    break;
                }
            }
        });
        Worker {
            id,
            thread: Some(thread),
        }
    }
}

type Job = Box<dyn FnOnce() + Send + 'static>;

enum Message {
    NewJob(Job),
    Terminate,
}

pub struct ThreadPool {
    workers: Vec<Worker>,
    sender: mpsc::Sender<Message>,
}

impl ThreadPool {
    /// Create a new ThreadPool.
    ///
    /// The size if the number of threads in the pool.
    ///
    /// # Panics
    ///
    /// The `new` function will panic if the size is zero.
    pub fn new(size: usize) -> Self {
        assert!(size > 0);

        let (sender, receiver) = mpsc::channel();
        let receiver = Arc::new(Mutex::new(receiver));

        let mut workers = Vec::with_capacity(size);
        for i in 0..size {
            workers.push(Worker::new(i, Arc::clone(&receiver)));
        }
        ThreadPool { workers, sender }
    }

    //F 的 trait bounds 怎么来的？答：参考 thread::spawn() 函数的参数定义
    pub fn execute<F>(&self, f: F)
    where
        F: FnOnce() + Send + 'static,
    {
        let job = Box::new(f);
        self.sender.send(Message::NewJob(job)).unwrap();
    }
}

impl Drop for ThreadPool {
    fn drop(&mut self) {
        //发送终止消息
        println!("*close server stage 1* Sending terminate message to all workers.");
        for _ in &mut self.workers {
            self.sender.send(Message::Terminate).unwrap();
        }

        //等待所有线程终止
        println!("*close server stage 2* Shutting down all workers.");
        for worker in &mut self.workers {
            println!("Shutting down worker-{}", worker.id);
            if let Some(thread) = worker.thread.take() {
                thread.join().unwrap();
            }
        }

        println!("*successful shutdown* Server is shutdown.");
    }
}
