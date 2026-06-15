use oop_demo::gui::*;
fn main() {
    let screen = Screen {
        components: vec![
            Box::new(Button {
                label: String::from("click me"),
            }),
            Box::new(TextFiled {
                content: String::from("I ate salad for breakfast."),
            }),
            // Box::new(String::from("hi")), //the trait bound `String: Draw` is not satisfied
        ],
    };
    screen.run();
}
