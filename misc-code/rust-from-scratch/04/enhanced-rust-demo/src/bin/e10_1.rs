fn main() {
    /*
        fn map<B, F>(self, f: F) -> Map<Self, F>
        where
            Self: Sized,
            F: FnMut(Self::Item) -> B,
        {
            Map::new(self, f)
        }
    */
    let list1 = vec![1, 2, 3];
    let list1: Vec<String> = list1.iter().map(|i| i.to_string()).collect();

    let list2 = vec![1, 2, 3];
    let list2: Vec<String> = list2.iter().map(ToString::to_string).collect();

    println!("{:?}, {:?}", list1, list2);
}
