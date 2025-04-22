package com.example.java_shop.viewmodels;

import android.app.Application;
import android.os.Bundle;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.java_shop.R;
import com.example.java_shop.data.models.Category;
import com.example.java_shop.data.repositories.CategoryRepository;
import java.util.List;

public class CategoriesViewModel extends AndroidViewModel {
    
    private final CategoryRepository categoryRepository;
    private final LiveData<List<Category>> categories;
    private final MutableLiveData<NavigationCommand> navigationCommand;

    public CategoriesViewModel(Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        categories = categoryRepository.getActiveCategories();
        navigationCommand = new MutableLiveData<>();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<NavigationCommand> getNavigationCommand() {
        return navigationCommand;
    }

    public void navigateToCategory(Category category) {
        if (category != null) {
            Bundle args = new Bundle();
            args.putString("categoryId", category.getId());
            navigationCommand.setValue(
                new NavigationCommand(
                    R.id.action_categoriesFragment_to_categoryDetailsFragment,
                    args
                )
            );
        }
    }

    public void resetNavigation() {
        navigationCommand.setValue(null);
    }

    public static class NavigationCommand {
        private final int actionId;
        private final Bundle args;

        public NavigationCommand(int actionId, Bundle args) {
            this.actionId = actionId;
            this.args = args;
        }

        public int getActionId() {
            return actionId;
        }

        public Bundle getArgs() {
            return args;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        categoryRepository.cleanup();
    }
}