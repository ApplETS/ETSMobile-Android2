// Generated code from Butter Knife. Do not modify!
package ca.etsmtl.applets.etsmobile.ui.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MenuAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final ca.etsmtl.applets.etsmobile.ui.adapter.MenuAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131034141);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131034141' for field 'title' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.title = (android.widget.TextView) view;
  }

  public static void reset(ca.etsmtl.applets.etsmobile.ui.adapter.MenuAdapter.ViewHolder target) {
    target.title = null;
  }
}
